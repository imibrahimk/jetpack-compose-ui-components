import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.khaliltech.dropdown_menu.ui.theme.DropdownMenuTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val dropdownViewModel: DropdownViewModel = viewModel()
            DropdownMenuTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        ReusableDropdownMenu(dropdownViewModel)
                    }
                }
            }
        }
    }
}

//==================================================================================================

@Composable
fun ReusableDropdownMenu(
    viewModel: DropdownViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem by viewModel.selectedItem.collectAsState()
    val dropdownItems by viewModel.dropdownItems.collectAsState()

    val isDarkTheme = isSystemInDarkTheme() // Detect system theme
    val colors = dropdownColors(isDarkTheme) // Get colors based on theme


    var buttonWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val cornerRadius = 8.dp

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(colors.backgroundColor, RoundedCornerShape(cornerRadius))
                .border(1.dp, colors.borderColor, shape = RoundedCornerShape(cornerRadius))
                .clip(RoundedCornerShape(cornerRadius))
                .clickable { expanded = true }
                .onGloballyPositioned { coordinates ->
                    buttonWidth = with(density) { coordinates.size.width.toDp() }
                },
            contentAlignment = Alignment.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = selectedItem,
                    color = colors.textColor,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f) // Pushes the arrow to the right
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown Arrow",
                    tint = colors.iconColor,
                    modifier = Modifier.size(20.dp) // Ensures proper sizing
                )

            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                shape = RoundedCornerShape(cornerRadius),
                modifier = Modifier
                    .width(buttonWidth) // Ensure same to same width button & items
                    .background(colors.itemBackground)
            ) {
                dropdownItems.forEach { item ->

                    val interactionSource = remember { MutableInteractionSource() }

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                fontSize = 16.sp,
                                color = colors.itemTextColor,
                                modifier = Modifier.fillMaxWidth() // Ensures full width
                            )
                        },
                        onClick = {
                            viewModel.onItemSelected(item)
                            expanded = false
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .background(colors.itemBackground)
                            .hoverable(interactionSource)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        this.tryAwaitRelease()
                                        colors.itemHoverColor
                                    }
                                )
                            }
                    )
                }
            }
        }
    }
}

// =================================================================================================

class DropdownViewModel : ViewModel() {

    private val _selectedItem = MutableStateFlow("Select an option")
    val selectedItem = _selectedItem.asStateFlow()

    private val _dropdownItems = MutableStateFlow(listOf("Apple", "Orange", "Jack fruit"))
    val dropdownItems = _dropdownItems.asStateFlow()

    fun onItemSelected(newItem: String) {
        _selectedItem.value = newItem
    }
}

//==================================================================================================

data class DropdownColors(
    val backgroundColor: Color,
    val textColor: Color,
    val borderColor: Color,
    val iconColor: Color,
    val itemBackground: Color,
    val itemTextColor: Color,
    val itemHoverColor: Color
)

@Composable
fun dropdownColors(isDarkTheme: Boolean): DropdownColors {
    return if (isDarkTheme) {
        DropdownColors(
            backgroundColor = Color(0xFF333333), // Dark Gray
            textColor = Color(0xFFE0E0E0), // Light Gray
            borderColor = Color(0xFF666666), // Medium Gray
            iconColor = Color(0xFFE0E0E0), // Light Gray
            itemBackground = Color(0xFF444444), // Slightly lighter gray
            itemTextColor = Color.White,
            itemHoverColor = Color(0xFF555555) // Hover effect color
        )
    } else {
        DropdownColors(
            backgroundColor = Color(0xFFF5F5F5), // Light Gray
            textColor = Color.Black,
            borderColor = Color(0xFFDADADA), // Light border
            iconColor = Color.Black,
            itemBackground = Color.White,
            itemTextColor = Color.Black,
            itemHoverColor = Color(0xFFEEEEEE) // Hover effect color
        )
    }
}
