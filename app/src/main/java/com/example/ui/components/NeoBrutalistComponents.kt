package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantBorderOutline
import com.example.ui.theme.VibrantPrimary
import com.example.ui.theme.VibrantPrimaryContainer
import com.example.ui.theme.VibrantSecondaryContainer
import com.example.ui.theme.VibrantShadow
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantTextDark
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary

/**
 * Vibrant Palette container Box with subtle border and soft elevation shadow.
 */
@Composable
fun NeoBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color = VibrantSurface,
    borderColor: Color = VibrantBorder,
    shadowColor: Color = VibrantShadow,
    borderWidth: Dp = 1.5.dp,
    shadowOffset: Dp = 3.dp,
    cornerRadius: Dp = 24.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(modifier = modifier) {
        // Drop Shadow layer
        if (shadowOffset > 0.dp) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(shadowColor, shape)
            )
        }

        // Foreground container layer
        Box(
            modifier = Modifier
                .background(backgroundColor, shape)
                .border(borderWidth, borderColor, shape)
                .clip(shape)
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

/**
 * Vibrant Palette interactive Button with tactile press animation.
 */
@Composable
fun NeoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = VibrantPrimaryContainer,
    contentColor: Color = VibrantTextDark,
    borderColor: Color = VibrantBorder,
    shadowOffset: Dp = 2.dp,
    cornerRadius: Dp = 16.dp,
    enabled: Boolean = true,
    testTag: String = "neo_button",
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shape = RoundedCornerShape(cornerRadius)
    val displayBg = if (enabled) backgroundColor else Color(0xFFE5E7EB)

    Box(
        modifier = modifier
            .testTag(testTag)
            .defaultMinSize(minHeight = 44.dp, minWidth = 44.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // Shadow
        if (shadowOffset > 0.dp && enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(VibrantShadow, shape)
            )
        }

        // Button Face
        Box(
            modifier = Modifier
                .offset(
                    x = if (isPressed && enabled) 1.dp else 0.dp,
                    y = if (isPressed && enabled) 1.dp else 0.dp
                )
                .background(displayBg, shape)
                .border(1.dp, borderColor, shape)
                .clip(shape)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

/**
 * Vibrant Palette Chip for pills, tags, levels, and filters.
 */
@Composable
fun NeoChip(
    text: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    selectedBg: Color = VibrantPrimary,
    unselectedBg: Color = VibrantSecondaryContainer,
    textColor: Color = if (isSelected) Color.White else VibrantTextDark,
    testTag: String = "neo_chip"
) {
    val shape = RoundedCornerShape(999.dp)
    val bg = if (isSelected) selectedBg else unselectedBg

    Box(
        modifier = modifier
            .testTag(testTag)
            .offset(y = if (isSelected) 1.dp else 0.dp)
    ) {
        // Chip Body
        Box(
            modifier = Modifier
                .background(bg, shape)
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) VibrantPrimary else VibrantBorder,
                    shape = shape
                )
                .clip(shape)
                .then(
                    if (onClick != null) Modifier.clickable(onClick = onClick)
                    else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = textColor
            )
        }
    }
}

/**
 * Vibrant Palette Input Field with modern rounded corners.
 */
@Composable
fun NeoInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    testTag: String = "neo_input_field"
) {
    val shape = RoundedCornerShape(16.dp)

    Box(modifier = modifier.testTag(testTag)) {
        // Input container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape)
                .border(1.5.dp, VibrantBorderOutline, shape)
                .clip(shape)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = LocalTextStyle.current.copy(
                            color = VibrantTextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = singleLine,
                    textStyle = TextStyle(
                        color = VibrantTextPrimary,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    cursorBrush = SolidColor(VibrantPrimary),
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (trailingIcon != null) {
                trailingIcon()
            }
        }
    }
}
