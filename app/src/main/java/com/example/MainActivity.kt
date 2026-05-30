package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.ScriptEntity
import com.example.ui.theme.HdBorderColor
import com.example.ui.theme.HdDarkText
import com.example.ui.theme.HdMutedGray
import com.example.ui.theme.HdPillBackground
import com.example.ui.theme.HdPrimaryBlue
import com.example.ui.theme.HdPureWhite
import com.example.ui.theme.HdSlateBackground
import com.example.ui.theme.HdSuccessGreen
import com.example.ui.theme.HdSyntaxKeyword
import com.example.ui.theme.HdSyntaxLiteral
import com.example.ui.theme.HdTerminalBg
import com.example.ui.theme.HdTerminalText
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ScriptViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ScriptViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(HdPureWhite)
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) { innerPadding ->
                    MainScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: ScriptViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HdSlateBackground)
    ) {
        // App Header styled with a sleek high density Top App Bar
        AppHeader()

        // Content Area (Flexible viewport height)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(HdSlateBackground)
        ) {
            when (selectedTab) {
                0 -> GeneratorScreen(viewModel)
                1 -> FavoritesScreen(viewModel)
                2 -> HistoryScreen(viewModel)
                3 -> TutorialScreen()
            }
        }

        // Beautiful MD3 bottom navigation bar with inline active pill shapes
        BottomNavigationBar(
            selectedTabIndex = selectedTab,
            onTabSelected = { viewModel.selectTab(it) }
        )
    }
}

@Composable
fun AppHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(HdPureWhite)
            .border(width = 1.dp, color = HdBorderColor, shape = RoundedCornerShape(0.dp))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Info & Status Indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Elegant circular CAD blueprint console logo (Terminal wrapper)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(HdPrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                // Custom drawn minimalist drafting angle bracket vector style
                Text(
                    text = "</>",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "مساعد AutoLISP الذكي",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = HdDarkText,
                    textAlign = TextAlign.Right
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(HdSuccessGreen)
                    )
                    Text(
                        text = "جاهز للبرمجة",
                        fontSize = 10.sp,
                        color = HdMutedGray
                    )
                }
            }
        }

        // Modern settings configuration accent button
        IconButton(
            onClick = { /* Settings action */ },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(HdSlateBackground)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "info",
                tint = HdMutedGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(HdPureWhite)
            .border(width = 1.dp, color = HdBorderColor, shape = RoundedCornerShape(0.dp))
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Navigation item mapping
        val navigationItems = listOf(
            NavigationData(0, "المحرر", "Editor", Icons.Default.PlayArrow),
            NavigationData(1, "المكتبة", "Library", Icons.Default.Favorite),
            NavigationData(2, "السجل", "History", Icons.Default.Search),
            NavigationData(3, "التشغيل", "Guide", Icons.Default.Info)
        )

        navigationItems.forEach { item ->
            val isSelected = selectedTabIndex == item.index
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = { onTabSelected(item.index) }
                    )
                    .testTag("bottom_nav_${item.index}"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Interactive active pill shape
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) HdPillBackground else Color.Transparent)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.labelAr,
                        tint = if (isSelected) HdDarkText else HdMutedGray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = item.labelAr,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) HdDarkText else HdMutedGray
                )
            }
        }
    }
}

data class NavigationData(
    val index: Int,
    val labelAr: String,
    val labelEn: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun GeneratorScreen(viewModel: ScriptViewModel) {
    val prompt by viewModel.inputPrompt.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generatedCode by viewModel.generatedCode.collectAsState()
    val generatedDesc by viewModel.generatedDescription.collectAsState()
    val errorMsg by viewModel.errorMessage.collectAsState()

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val scrollState = rememberScrollState()

    // Interactive suggest chips list
    val suggestionChips = listOf(
        "رسم مربع 50 في 50",
        "تغيير ألوان الخطوط المحددة للأحمر",
        "حساب مجموع أطوال الخطوط",
        "رسم شبكة دوائر",
        "تنظيف اللوحة"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // High Density Introduction Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = HdPureWhite),
            border = BorderStroke(width = 1.dp, color = HdBorderColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "🤖 صانع أكواد AutoLISP بالذكاء الاصطناعي",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HdDarkText,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "اكتب باللغة العربية البسيطة ما تريد تصميمه أو تعديله في AutoCAD، وثوانٍ معدودة وسيتم تحويله إلى أمر متكامل جاهز للتنفيذ داخل البرنامج.",
                    fontSize = 11.sp,
                    color = HdMutedGray,
                    textAlign = TextAlign.Right,
                    lineHeight = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Chat Viewport Interactive Thread Representation
        if (generatedCode != null || errorMsg != null || isGenerating) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. User Message (Representing prompt as an active high density speech bubble)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp))
                            .background(HdPillBackground)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = prompt.ifBlank { "طلب رسم مربع أوتوكاد..." },
                            color = HdDarkText,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Right
                        )
                    }
                    Text(
                        text = "10:42 AM", // Mocked high density chat stamp
                        fontSize = 9.sp,
                        color = HdMutedGray,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }

                // 2. Generating Progress State
                if (isGenerating) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = HdPureWhite),
                        border = BorderStroke(width = 1.dp, color = HdBorderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = HdPrimaryBlue,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "جاري صياغة أمر AutoLISP المناسب...",
                                fontSize = 12.sp,
                                color = HdDarkText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 3. AI Generated Code Block Response Focus
                if (generatedCode != null && !isGenerating) {
                    val code = generatedCode ?: ""
                    val desc = generatedDesc ?: ""

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        // Main code terminal card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = HdTerminalBg),
                            border = BorderStroke(width = 1.dp, color = Color(0xFF334155)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                // Top header menu
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0F172A))
                                        .border(
                                            BorderStroke(width = 0.5.dp, color = Color(0xFF334155)),
                                            RoundedCornerShape(0.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "AutoLISP Script (.LSP)",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = HdTerminalText,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // "نسخ" Copy button directly inside
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF1E293B))
                                                .clickable {
                                                    clipboardManager.setText(AnnotatedString(code))
                                                    Toast.makeText(context, "تم نسخ كود AutoLISP!", Toast.LENGTH_SHORT).show()
                                                }
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "نسخ الكود",
                                                fontSize = 10.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Copy",
                                                tint = HdTerminalText,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                val sendIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, code)
                                                    type = "text/plain"
                                                }
                                                context.startActivity(Intent.createChooser(sendIntent, "مشاركة الملف البرمجي"))
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Share",
                                                tint = Color.LightGray,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                // Interactive Syntax Highlighting code container
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    SyntaxHighlightedText(code = code)
                                }

                                // Interactive footer details pane
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF1E293B))
                                        .border(
                                            BorderStroke(width = 0.5.dp, color = Color(0xFF334155)),
                                            RoundedCornerShape(0.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = detectPrimaryCommand(code),
                                        fontSize = 10.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = Color.LightGray,
                                        fontFamily = FontFamily.Monospace,
                                        textAlign = TextAlign.Left
                                    )
                                    Text(
                                        text = "توجيهات التشغيل بالأسفل ⚙️",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HdTerminalText
                                    )
                                }
                            }
                        }

                        // Code details / manual tips below the block
                        if (desc.isNotEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = HdPureWhite),
                                border = BorderStroke(width = 1.dp, color = HdBorderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "💡 طريقة الاستخدام والتحميل:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HdDarkText,
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        color = HdMutedGray,
                                        lineHeight = 16.sp,
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        // Clear generation container
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { viewModel.clearLastGeneration() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                modifier = Modifier.height(32.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp)
                            ) {
                                Text("إخفاء النتيجة", fontSize = 10.sp, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }

        // Connection API Key Setup Help
        if (errorMsg != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "⚠️ لم يتم تفعيل مفتاح الربط الذكي API Key",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB91C1C),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "يرجى نسخ مفتاح API الخاص بك من موقع Google AI Studio ولصقه في تبويب الإعدادات لتفعيل توليد الذكاء الاصطناعي بنجاح.",
                        fontSize = 11.sp,
                        color = Color(0xFF7F1D1D),
                        textAlign = TextAlign.Right,
                        lineHeight = 16.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Prompt Input Configuration Block and suggest tags
        Card(
            colors = CardDefaults.cardColors(containerColor = HdPureWhite),
            border = BorderStroke(width = 1.dp, color = HdBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "اكتب طلبك لـ AutoLISP بلغة طبيعية:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = HdDarkText,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { viewModel.updateInputPrompt(it) },
                        placeholder = {
                            Text(
                                text = "مثال: رسم مربع أبعاده 50 في 50...",
                                style = TextStyle(
                                    textDirection = TextDirection.ContentOrRtl,
                                    textAlign = TextAlign.Right,
                                    fontSize = 13.sp
                                ),
                                color = HdMutedGray,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 90.dp)
                            .testTag("prompt_input_field"),
                        textStyle = TextStyle(
                            color = HdDarkText,
                            fontSize = 13.sp,
                            textDirection = TextDirection.ContentOrRtl,
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HdPrimaryBlue,
                            unfocusedBorderColor = HdBorderColor,
                            unfocusedContainerColor = HdSlateBackground,
                            focusedContainerColor = HdSlateBackground
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )

                    // Right Float action send rocket launcher
                    IconButton(
                        onClick = {
                            if (prompt.trim().isNotEmpty()) {
                                focusManager.clearFocus()
                                viewModel.createAutoLispScript()
                            } else {
                                Toast.makeText(context, "يرجى كتابة سيناريو أولاً", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(HdPrimaryBlue)
                            .testTag("generate_lisp_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow, // Aligned navigation trigger
                            contentDescription = "Generate",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Suggest Tags Row
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "💡 طلبات مقترحة سريعة:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HdMutedGray,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Render suggestions horizontally (takes modern multirow flow or compact scroll)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            suggestionChips.take(3).forEach { suggest ->
                                Box(
                                    modifier = Modifier
                                        .background(HdSlateBackground, RoundedCornerShape(16.dp))
                                        .border(1.dp, HdBorderColor, RoundedCornerShape(16.dp))
                                        .clickable {
                                            viewModel.updateInputPrompt(suggest)
                                            focusManager.clearFocus()
                                            viewModel.createAutoLispScript()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = suggest,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HdDarkText
                                    )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            suggestionChips.drop(3).forEach { suggest ->
                                Box(
                                    modifier = Modifier
                                        .background(HdSlateBackground, RoundedCornerShape(16.dp))
                                        .border(1.dp, HdBorderColor, RoundedCornerShape(16.dp))
                                        .clickable {
                                            viewModel.updateInputPrompt(suggest)
                                            focusManager.clearFocus()
                                            viewModel.createAutoLispScript()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = suggest,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HdDarkText
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SyntaxHighlightedText(code: String) {
    val keywords = listOf(
        "defun", "c:", "setq", "if", "progn", "repeat", "while", 
        "vl-load-com", "command", "princ", "rtos", "alert", "strcat"
    )

    val annotatedString = remember(code) {
        val builder = AnnotatedString.Builder()
        var index = 0

        while (index < code.length) {
            val char = code[index]

            // Highlight double quotes strings
            if (char == '\"') {
                builder.pushStyle(androidx.compose.ui.text.SpanStyle(color = HdSyntaxLiteral))
                builder.append(char)
                index++
                while (index < code.length && code[index] != '\"') {
                    builder.append(code[index])
                    index++
                }
                if (index < code.length) {
                    builder.append(code[index])
                    index++
                }
                builder.pop()
                continue
            }

            // Highlight comments (starting with semicolon)
            if (char == ';') {
                builder.pushStyle(androidx.compose.ui.text.SpanStyle(color = Color(0xFF64748B), fontStyle = FontStyle.Italic))
                while (index < code.length && code[index] != '\n') {
                    builder.append(code[index])
                    index++
                }
                builder.pop()
                continue
            }

            // Highlight keywords vs plain code parameters
            if (char.isLetterOrDigit() || char == ':' || char == '-' || char == '_') {
                val start = index
                while (index < code.length && (code[index].isLetterOrDigit() || code[index] == ':' || code[index] == '-' || code[index] == '_')) {
                    index++
                }
                val word = code.substring(start, index)

                if (keywords.contains(word.lowercase()) || word.lowercase().startsWith("c:")) {
                    builder.pushStyle(androidx.compose.ui.text.SpanStyle(color = HdSyntaxKeyword, fontWeight = FontWeight.Bold))
                    builder.append(word)
                    builder.pop()
                } else {
                    builder.append(word)
                }
                continue
            }

            // Visual brightness enhancement for parentheses
            if (char == '(' || char == ')') {
                builder.pushStyle(androidx.compose.ui.text.SpanStyle(color = Color(0xFF94A3B8)))
                builder.append(char)
                builder.pop()
            } else {
                builder.append(char)
            }
            index++
        }
        builder.toAnnotatedString()
    }

    Text(
        text = annotatedString,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        color = HdTerminalText,
        lineHeight = 16.sp,
        modifier = Modifier.fillMaxWidth()
    )
}

fun detectPrimaryCommand(code: String): String {
    val defunRegex = "defun c:([^\\s\\(\\)]+)".toRegex(RegexOption.IGNORE_CASE)
    val match = defunRegex.find(code)
    return if (match != null) {
        "Command: ${match.groupValues[1]}"
    } else {
        "Command: AUTO_LISP"
    }
}

@Composable
fun FavoritesScreen(viewModel: ScriptViewModel) {
    val favorites by viewModel.favoriteScripts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // High Density Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("بحث في المفضلة...", color = HdMutedGray, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "", tint = HdMutedGray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .testTag("favorites_search_field"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HdPrimaryBlue,
                unfocusedBorderColor = HdBorderColor,
                unfocusedContainerColor = HdPureWhite,
                focusedContainerColor = HdPureWhite
            ),
            singleLine = true
        )

        if (favorites.isEmpty()) {
            EmptyStateView(
                title = "المفضلة فارغة حالياً",
                subtitle = "قم بحفظ الأكواد الهامة بلمس النجمة الصفراء للرجوع السريع لها."
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                favorites.forEach { script ->
                    ScriptHistoryCard(
                        script = script,
                        onLoadClick = { viewModel.loadScriptToViewer(script) },
                        onFavoriteToggle = { viewModel.toggleFavorite(script) },
                        onDeleteClick = { viewModel.deleteScript(script) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(viewModel: ScriptViewModel) {
    val history by viewModel.allScripts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (history.isNotEmpty()) {
                IconButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        Toast.makeText(context, "تم مسح سجل العمليات.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear all", tint = Color(0xFFFF5252))
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("بحث في السجل...", color = HdMutedGray, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "", tint = HdMutedGray) },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .testTag("history_search_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HdPrimaryBlue,
                    unfocusedBorderColor = HdBorderColor,
                    unfocusedContainerColor = HdPureWhite,
                    focusedContainerColor = HdPureWhite
                ),
                singleLine = true
            )
        }

        if (history.isEmpty()) {
            EmptyStateView(
                title = "السجل فارغ",
                subtitle = "لم تقم بتوليد أي كود بعد، أدخل وصفاً برمجياً في المحرر للبدء."
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                history.forEach { script ->
                    ScriptHistoryCard(
                        script = script,
                        onLoadClick = { viewModel.loadScriptToViewer(script) },
                        onFavoriteToggle = { viewModel.toggleFavorite(script) },
                        onDeleteClick = { viewModel.deleteScript(script) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScriptHistoryCard(
    script: ScriptEntity,
    onLoadClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = HdPureWhite),
        border = BorderStroke(width = 1.dp, color = HdBorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLoadClick() }
            .testTag("script_card_${script.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Card visual header with title and favorites control
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onFavoriteToggle) {
                        Icon(
                            imageVector = if (script.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (script.isFavorite) Color(0xFFFF5252) else HdMutedGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Clear, contentDescription = "Delete", tint = HdMutedGray, modifier = Modifier.size(16.dp))
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = script.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HdDarkText
                    )
                    Text(
                        text = script.category,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = HdPrimaryBlue,
                        modifier = Modifier
                            .background(HdPillBackground, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = script.prompt,
                fontSize = 11.sp,
                color = HdDarkText,
                maxLines = 2,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Inline code preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HdTerminalBg, RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = script.code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = HdTerminalText,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(script.code))
                        Toast.makeText(context, "تم نسخ الكود بنجاح للأوتوكاد 📋", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HdSlateBackground),
                    modifier = Modifier.height(28.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                ) {
                    Text("نسخ الكود 📋", fontSize = 9.sp, color = HdPrimaryBlue, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm a", script.timestamp).toString(),
                    fontSize = 9.sp,
                    color = HdMutedGray
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "",
            tint = HdPrimaryBlue.copy(alpha = 0.4f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = HdDarkText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontSize = 11.sp,
            color = HdMutedGray,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TutorialScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = HdPureWhite),
            border = BorderStroke(1.dp, HdBorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⚙️ كيف تشغل أكواد AutoLISP في تطبيق AutoCAD؟",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HdDarkText,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "برمجة AutoLISP هي الطريقة الأكثر فاعلية لتسريع وتخصيص سير العمل في AutoCAD لتنفيذ المهام المكررة ببضع ثوانٍ.",
                    fontSize = 11.sp,
                    color = HdMutedGray,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Accordions
        TutorialStepCard(
            stepNumber = "1",
            titleArabic = "انسخ الكود من هذا التطبيق",
            titleEnglish = "Copy the Generated Code",
            desc = "اختر أي كود تبغاه من تبويب المولد أو من السجل، واضغط على زر 'نسخ الكود' لحفظ النص في الحافظة."
        )

        TutorialStepCard(
            stepNumber = "2",
            titleArabic = "شغّل AutoCAD وافتح سطر الأوامر",
            titleEnglish = "Open AutoCAD Command Line",
            desc = "في AutoCAD، انظر لسطر الأوامر (Command Bar) بالأسفل. يمكنك إلصاق (Paste) الكود المنسوخ مباشرة هناك وضغط Enter ليصبح الأمر فعالاً لجلسة الرسم الحالية!"
        )

        TutorialStepCard(
            stepNumber = "3",
            titleArabic = "تشغيل الأمر عن طريق اسمه المختصر",
            titleEnglish = "Run Using Command Name",
            desc = "انظر للاسم بعد 'defun c:NAME'. هذا هو اسم الأمر. مثلاً، إذا كان الكود يحتوي على '(defun c:DrawSquare50 ...)' فقط اكتب DrawSquare50 في سطر الأوامر واضغط Enter للتنفيذ!"
        )

        TutorialStepCard(
            stepNumber = "4",
            titleArabic = "أو قم بتحميل مفضلتك كملف LSP",
            titleEnglish = "Save as .LSP file",
            desc = "إذا كنت تبغى استدعاء الأوامر باستمرار، احفظ النص بملف نصي عادي بالكمبيوتر وغير امتداده من txt إلى (lsp.)، ثم داخل AutoCAD اكتب أمر APPLOAD واستدعي الملف وسيشتغل دائمًا مع كل لوحة تفتحها!"
        )
    }
}

@Composable
fun TutorialStepCard(
    stepNumber: String,
    titleArabic: String,
    titleEnglish: String,
    desc: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = HdPureWhite),
        border = BorderStroke(1.dp, HdBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = titleArabic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = HdDarkText,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = titleEnglish,
                    fontSize = 10.sp,
                    color = HdPrimaryBlue,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = HdMutedGray,
                    textAlign = TextAlign.Right,
                    lineHeight = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(HdPillBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = HdDarkText
                )
            }
        }
    }
}
