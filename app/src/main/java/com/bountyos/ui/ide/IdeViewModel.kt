package com.bountyos.ui.ide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bountyos.domain.model.ScriptLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * IDE 屏幕状态。
 */
data class IdeUiState(
    val code: String = "",
    val language: ScriptLanguage = ScriptLanguage.RUBY,
    val isRunning: Boolean = false,
    val stdout: String = "",
    val stderr: String = "",
    val runError: String? = null,
)

/**
 * IDE 的 ViewModel。
 *
 * 管理代码、语言与执行状态，执行通过 [CodeExecutor] 在后台完成。
 */
@HiltViewModel
class IdeViewModel @Inject constructor(
    private val executor: CodeExecutor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(IdeUiState())
    val uiState: StateFlow<IdeUiState> = _uiState.asStateFlow()

    fun onCodeChange(code: String) = _uiState.update { it.copy(code = code) }

    fun onLanguageSelect(language: ScriptLanguage) =
        _uiState.update { it.copy(language = language) }

    fun reset() = _uiState.update {
        it.copy(code = "", stdout = "", stderr = "", runError = null)
    }

    fun run() {
        val snapshot = _uiState.value
        if (snapshot.code.isBlank() || snapshot.isRunning) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true, stdout = "", stderr = "", runError = null) }
            runCatching { executor.execute(snapshot.code, snapshot.language) }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(isRunning = false, stdout = result.stdout, stderr = result.stderr)
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isRunning = false, runError = e.message) }
                }
        }
    }
}
