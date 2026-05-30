package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.api.GenerateResult
import com.example.database.ScriptDatabase
import com.example.database.ScriptEntity
import com.example.database.ScriptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScriptViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ScriptRepository

    init {
        val database = ScriptDatabase.getDatabase(application)
        repository = ScriptRepository(database.scriptDao())
        
        // Seed the database with high-quality AutoCAD AutoLISP templates if empty
        seedDatabaseIfEmpty()
    }

    // Tab state (0 = Generator, 1 = History, 2 = Favorites, 3 = Tutorial)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    // Generator Tab State
    private val _inputPrompt = MutableStateFlow("")
    val inputPrompt: StateFlow<String> = _inputPrompt.asStateFlow()

    fun updateInputPrompt(text: String) {
        _inputPrompt.value = text
    }

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generatedCode = MutableStateFlow<String?>(null)
    val generatedCode: StateFlow<String?> = _generatedCode.asStateFlow()

    private val _generatedDescription = MutableStateFlow<String?>(null)
    val generatedDescription: StateFlow<String?> = _generatedDescription.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // History Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Reactive streams for all scripts and favorite scripts
    val allScripts: StateFlow<List<ScriptEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allScripts
            } else {
                repository.searchScripts(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteScripts: StateFlow<List<ScriptEntity>> = repository.favoriteScripts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Call the Gemini API to translate Arabic or English text to AutoLISP code
     */
    fun createAutoLispScript() {
        val prompt = _inputPrompt.value.trim()
        if (prompt.isEmpty()) return

        _isGenerating.value = true
        _errorMessage.value = null
        _generatedCode.value = null
        _generatedDescription.value = null

        viewModelScope.launch {
            when (val result = GeminiClient.generateAutoLisp(prompt)) {
                is GenerateResult.Success -> {
                    _generatedCode.value = result.code
                    _generatedDescription.value = result.description
                    
                    // Generate a concise title for database entry
                    val generatedTitle = suggestTitle(prompt, result.code)
                    
                    // Save to local database history
                    val category = detectCategory(result.code)
                    repository.insertScript(
                        ScriptEntity(
                            title = generatedTitle,
                            prompt = _inputPrompt.value,
                            code = result.code,
                            category = category
                        )
                    )
                }
                is GenerateResult.Error -> {
                    _errorMessage.value = result.message
                }
            }
            _isGenerating.value = false
        }
    }

    /**
     * Quick loading of any script into the active viewer
     */
    fun loadScriptToViewer(script: ScriptEntity) {
        _inputPrompt.value = script.prompt
        _generatedCode.value = script.code
        _generatedDescription.value = "تم استدعاؤها من السجل / Loaded from database record."
        _selectedTab.value = 0 // Switch to Generator View to view & copy
    }

    /**
     * Database CRUD actions
     */
    fun deleteScript(script: ScriptEntity) = viewModelScope.launch {
        repository.deleteScript(script)
    }

    fun toggleFavorite(script: ScriptEntity) = viewModelScope.launch {
        repository.updateScript(script.copy(isFavorite = !script.isFavorite))
    }

    fun clearAllHistory() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun clearLastGeneration() {
        _generatedCode.value = null
        _generatedDescription.value = null
        _errorMessage.value = null
    }

    private fun detectCategory(code: String): String {
        return when {
            code.contains("_.RECTANG", true) || code.contains("_.CIRCLE", true) || code.contains("_.LINE", true) -> "رسم / Draw"
            code.contains("._chprop", true) || code.contains("chprop", true) || code.contains("command \".\"", true) -> "تعديل / Modify"
            code.contains("total", true) || code.contains("dist", true) || code.contains("+", true) -> "حسابات / Stats"
            else -> "مساعدة / Utility"
        }
    }

    private fun suggestTitle(prompt: String, code: String): String {
        // Look within the code for defun c:NAME
        val defunRegex = "defun c:([^\\s\\(\\)]+)".toRegex(RegexOption.IGNORE_CASE)
        val match = defunRegex.find(code)
        if (match != null) {
            val funcName = match.groupValues[1]
            return "Command: $funcName"
        }

        // Fallback to prompt substring
        val cleanPrompt = prompt.trim()
        return if (cleanPrompt.length > 25) {
            cleanPrompt.substring(0, 22) + "..."
        } else {
            cleanPrompt
        }
    }

    private fun seedDatabaseIfEmpty() {
        viewModelScope.launch {
            repository.allScripts.collect { list ->
                if (list.isEmpty()) {
                    // Seed Template 1: Line Length Sum (حساب أطوال العناصر)
                    repository.insertScript(
                        ScriptEntity(
                            title = "Command: SumLengths",
                            prompt = "أريد طريقة سريعة لحساب مجموع أطوال عدة خطوط مستقيمة في اللوحة",
                            code = """(defun c:SumLengths (/ ss total i en len)
  ;; تفعيل بيئة ActiveX
  (vl-load-com)
  ;; اختيار الخطوط، المنحنيات، والدوائر
  (setq ss (ssget '((0 . "LINE,POLYLINE,LWPOLYLINE,ARC,CIRCLE,ELLIPSE,SPLINE"))))
  (if ss
    (progn
      (setq total 0.0 i 0)
      (repeat (sslength ss)
        (setq en (ssname ss i))
        ;; حساب المسافة من البداية للنهاية لكل عنصر
        (setq len (vlax-curve-getDistAtParam en (vlax-curve-getEndParam en)))
        (setq total (+ total len))
        (setq i (1+ i))
      )
      ;; إظهار رسالة منبثقة بالنتيجة وطباعتها في سطر الأوامر
      (alert (strcat "مجموع الأطوال للعناصر المختارة هو: " (rtos total 2 2)))
      (princ (strcat "\nمجموع الأطوال الكلي: " (rtos total 2 2)))
    )
    (princ "\nلم يتم اختيار أي عناصر صالحة.")
  )
  (princ)
)""",
                            isFavorite = true,
                            category = "حسابات / Stats"
                        )
                    )

                    // Seed Template 2: Custom red color lines (تلوين الخطوط المحددة بالأحمر)
                    repository.insertScript(
                        ScriptEntity(
                            title = "Command: ChangeToRed",
                            prompt = "اكتب لي أمر ليسب يقوم بتغيير لون جميع الخطوط المحددة إلى اللون الأحمر",
                            code = """(defun c:ChangeToRed (/ ss)
  ;; اختيار عناصر من نوع خطوط مستقيمة فقط
  (setq ss (ssget '((0 . "LINE"))))
  (if ss
    (progn
      ;; تغيير الخاصية Color (C) إلى الرقم 1 وهو اللون الأحمر
      (command "._chprop" ss "" "C" "1" "")
      (princ "\nتم بنجاح تغيير لون الخطوط المحددة إلى اللون الأحمر.")
    )
    (princ "\nلم يتم اختيار أي خطوط.")
  )
  (princ)
)""",
                            isFavorite = false,
                            category = "تعديل / Modify"
                        )
                    )

                    // Seed Template 3: Drawing 50x50 Square (رسم مربع 50x50 في نقطة الأصل)
                    repository.insertScript(
                        ScriptEntity(
                            title = "Command: DrawSquare50",
                            prompt = "أريد كوداً يقوم برسم مربع أبعاده 50 في 50 في نقطة الأصل (0,0)",
                            code = """(defun c:DrawSquare50 ()
  ;; رسم مستطيل يبدأ من 0,0 وينتهي في 50,50
  (command "_.RECTANG" "0,0" "50,50")
  (princ "\nتم رسم المربع 50x50 في نقطة الأصل.")
  (princ)
)""",
                            isFavorite = true,
                            category = "رسم / Draw"
                        )
                    )

                    // Seed Template 4: Draw a responsive Circle Grid (رسم شبكة دوائر)
                    repository.insertScript(
                        ScriptEntity(
                            title = "Command: CircleGrid",
                            prompt = "رسم شبكة من الدوائر بمسافات متساوية",
                            code = """(defun c:CircleGrid (/ rows cols spacing radius startX startY r c px py)
  (setq rows 5)
  (setq cols 5)
  (setq spacing 100.0)
  (setq radius 15.0)
  (setq startX 0.0)
  (setq startY 0.0)
  (setq r 0)
  (while (< r rows)
    (setq c 0)
    (while (< c cols)
      (setq px (+ startX (* c spacing)))
      (setq py (+ startY (* r spacing)))
      (command "_.CIRCLE" (list px py 0) radius)
      (setq c (1+ c))
    )
    (setq r (1+ r))
  )
  (princ "\nتم رسم شبكة الدوائر 5x5 بنجاح بمسافة 100.")
  (princ)
)""",
                            isFavorite = false,
                            category = "رسم / Draw"
                        )
                    )
                }
            }
        }
    }
}
