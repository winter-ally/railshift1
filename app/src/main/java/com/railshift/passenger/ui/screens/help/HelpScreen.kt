package com.railshift.passenger.ui.screens.help

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.railshift.passenger.ui.components.TabTopBar
import com.railshift.passenger.ui.theme.RailshiftTheme

@Composable
fun HelpScreen(
    viewModel: HelpViewModel,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showTopicDialog by remember { mutableStateOf<String?>(null) }

    HelpContent(
        uiState = uiState,
        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
        onCallClick = {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:139"))
            context.startActivity(intent)
        },
        onChatClick = {
            showTopicDialog = "Railshift 24x7 Support Chat: Our support assistant is online to help you with bookings and refunds."
        },
        onEmailClick = {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@railshift.in"))
            context.startActivity(intent)
        },
        onTopicClick = { topicName ->
            showTopicDialog = "$topicName: Helpful guidelines and FAQs on ticket cancellation, refunds, and live tracking are available in this section."
        },
        onToggleFaq = { viewModel.toggleFaq(it) },
        onOpenLanguagePicker = onOpenLanguagePicker,
        modifier = modifier
    )

    if (showTopicDialog != null) {
        val colors = RailshiftTheme.colors
        AlertDialog(
            onDismissRequest = { showTopicDialog = null },
            title = {
                Text(
                    text = "Help Topic",
                    fontWeight = FontWeight.Bold,
                    color = colors.text
                )
            },
            text = {
                Text(
                    text = showTopicDialog.orEmpty(),
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showTopicDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("OK", color = colors.onAccent)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun HelpContent(
    uiState: HelpUiState,
    onSearchQueryChange: (String) -> Unit,
    onCallClick: () -> Unit,
    onChatClick: () -> Unit,
    onEmailClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onToggleFaq: (String) -> Unit,
    onOpenLanguagePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        TabTopBar(onLanguageClick = onOpenLanguagePicker)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp)
                .padding(bottom = 16.dp)
        ) {
            // Title
            Text(
                text = stringResource(R.string.nav_help),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                letterSpacing = (-0.3).sp,
                modifier = Modifier.padding(top = 6.dp, start = 4.dp)
            )

            // Search input field
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, colors.borderStrong, RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .padding(horizontal = 12.dp, vertical = 11.dp)
                    .testTag("help_search_field"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.search_help_placeholder),
                    fontSize = 14.sp,
                    color = colors.textSecondary
                )
            }

            // Talk to us section
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.section_talk_to_us),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ContactActionTile(
                    label = stringResource(R.string.contact_call),
                    icon = Icons.Outlined.Phone,
                    onClick = onCallClick,
                    modifier = Modifier.weight(1f).testTag("help_contact_call")
                )
                ContactActionTile(
                    label = stringResource(R.string.contact_chat),
                    icon = Icons.Outlined.ChatBubbleOutline,
                    onClick = onChatClick,
                    modifier = Modifier.weight(1f).testTag("help_contact_chat")
                )
                ContactActionTile(
                    label = stringResource(R.string.contact_email),
                    icon = Icons.Outlined.MailOutline,
                    onClick = onEmailClick,
                    modifier = Modifier.weight(1f).testTag("help_contact_email")
                )
            }

            // Browse topics section
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.section_browse_topics),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val topics = listOf(
                    Pair(stringResource(R.string.topic_booking), Icons.Outlined.ConfirmationNumber),
                    Pair(stringResource(R.string.topic_refunds), Icons.Outlined.CreditCard),
                    Pair(stringResource(R.string.topic_pnr), Icons.Outlined.FactCheck),
                    Pair(stringResource(R.string.topic_payments), Icons.Outlined.AccountBalanceWallet),
                    Pair(stringResource(R.string.topic_qr), Icons.Outlined.QrCodeScanner)
                )

                topics.forEach { (topicName, icon) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                            .background(colors.surface)
                            .clickable { onTopicClick(topicName) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("help_topic_${topicName.lowercase().replace(" ", "_")}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(colors.surface2),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = colors.text,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = topicName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.text,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Common questions section
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.section_common_questions),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .padding(horizontal = 12.dp)
            ) {
                uiState.faqItems.forEachIndexed { index, faq ->
                    if (index > 0) {
                        HorizontalDivider(thickness = 1.dp, color = colors.border)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleFaq(faq.id) }
                            .padding(vertical = 12.dp)
                            .testTag("faq_item_${faq.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(faq.questionRes),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.text,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (faq.isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                                contentDescription = null,
                                tint = colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        AnimatedVisibility(visible = faq.isExpanded) {
                            Text(
                                text = stringResource(faq.answerRes),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = colors.textSecondary,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactActionTile(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RailshiftTheme.colors

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surface2)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(colors.accentContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.text
        )
    }
}

@Preview(name = "Help Screen Light")
@Composable
private fun HelpScreenLightPreview() {
    RailshiftTheme(darkTheme = false) {
        HelpContent(
            uiState = HelpUiState(
                faqItems = listOf(
                    FaqItem("1", R.string.faq_q1, R.string.faq_a1, isExpanded = true),
                    FaqItem("2", R.string.faq_q2, R.string.faq_a2, isExpanded = false)
                )
            ),
            onSearchQueryChange = {},
            onCallClick = {},
            onChatClick = {},
            onEmailClick = {},
            onTopicClick = {},
            onToggleFaq = {},
            onOpenLanguagePicker = {}
        )
    }
}

@Preview(name = "Help Screen Dark")
@Composable
private fun HelpScreenDarkPreview() {
    RailshiftTheme(darkTheme = true) {
        HelpContent(
            uiState = HelpUiState(
                faqItems = listOf(
                    FaqItem("1", R.string.faq_q1, R.string.faq_a1, isExpanded = true),
                    FaqItem("2", R.string.faq_q2, R.string.faq_a2, isExpanded = false)
                )
            ),
            onSearchQueryChange = {},
            onCallClick = {},
            onChatClick = {},
            onEmailClick = {},
            onTopicClick = {},
            onToggleFaq = {},
            onOpenLanguagePicker = {}
        )
    }
}
