package com.bountyos.ui.mitre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bountyos.domain.model.MitreMatrix
import com.bountyos.domain.model.MitreTactic
import com.bountyos.domain.model.MitreTechnique
import com.bountyos.domain.repository.MitreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 单个战术及其下辖技术，供矩阵列表渲染。 */
data class MitreTacticGroup(
    val tactic: MitreTactic,
    val techniques: List<MitreTechnique>,
)

data class MitreUiState(
    val isLoading: Boolean = false,
    val matrix: MitreMatrix? = null,
    val groups: List<MitreTacticGroup> = emptyList(),
    val query: String = "",
    val hasError: Boolean = false,
) {
    /** 本地无缓存且未在加载、也未出错：需要用户主动触发下载。 */
    val needsDownload: Boolean get() = matrix == null && !isLoading && !hasError
}

/**
 * MITRE ATT&CK 矩阵的界面状态。
 *
 * 进入页面只读本地缓存；官方数据集体积较大，首次浏览由用户显式
 * 点击下载，不在后台静默拉取。
 */
@HiltViewModel
class MitreViewModel @Inject constructor(
    private val repository: MitreRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MitreUiState())
    val uiState: StateFlow<MitreUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val cached = repository.loadCached() ?: return@launch
            _uiState.update {
                it.copy(
                    matrix = cached,
                    groups = group(cached.tactics, cached.techniques),
                )
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { state ->
            val matrix = state.matrix ?: return@update state.copy(query = query)
            state.copy(
                query = query,
                groups = group(matrix.tactics, matrix.techniques, query),
            )
        }
    }

    /** 下载最新数据并重建缓存。 */
    fun download() {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hasError = false) }
            runCatching { repository.refresh() }
                .onSuccess { matrix ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            matrix = matrix,
                            groups = group(matrix.tactics, matrix.techniques, it.query),
                            hasError = false,
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, hasError = true) }
                }
        }
    }

    /**
     * 按战术分组技术。子技术单独置于其战术内、按 ID 排在父技术之后，
     * 由界面以缩进区分。
     */
    private fun group(
        tactics: List<MitreTactic>,
        techniques: List<MitreTechnique>,
        query: String = "",
    ): List<MitreTacticGroup> {
        val keyword = query.trim()
        val visible = if (keyword.isEmpty()) {
            techniques
        } else {
            techniques.filter {
                it.id.contains(keyword, ignoreCase = true) ||
                    it.name.contains(keyword, ignoreCase = true)
            }
        }

        val byTactic = visible
            .flatMap { technique -> technique.tacticShortNames.map { it to technique } }
            .groupBy({ it.first }, { it.second })

        return tactics.mapNotNull { tactic ->
            val items = byTactic[tactic.shortName]
                ?.sortedWith(compareBy({ it.isSubTechnique }, { it.id }))
            if (items.isNullOrEmpty()) null else MitreTacticGroup(tactic = tactic, techniques = items)
        }
    }
}
