package com.example.presentation.markets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.components.GlassCard
import com.example.ui.theme.MayaAmberNeon
import com.example.ui.theme.MayaCardBorder
import com.example.ui.theme.MayaCyanNeon
import com.example.ui.theme.MayaGreenNeon
import com.example.ui.theme.MayaNavyBackground
import com.example.ui.theme.MayaRedNeon
import com.example.ui.theme.MayaTextMuted
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary

data class MarketItem(
    val symbol: String,
    val name: String,
    val price: String,
    val change: String,
    val isPositive: Boolean
)

@Composable
fun MarketsScreen(
    onNavigateBack: () -> Unit
) {
    val sampleMarkets = listOf(
        MarketItem("S&P 500", "US 500 Index", "$5,885.20", "+0.42%", true),
        MarketItem("NASDAQ", "Tech Composite", "$18,450.80", "+0.78%", true),
        MarketItem("BTC/USD", "Bitcoin", "$96,400.00", "+3.15%", true),
        MarketItem("ETH/USD", "Ethereum", "$2,740.50", "-1.20%", false),
        MarketItem("XAU/USD", "Gold Spot", "$2,710.15", "+0.34%", true),
        MarketItem("CRUDE", "WTI Crude Oil", "$71.40", "-0.85%", false)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MayaNavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("markets_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MayaTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Financial Markets & Quotes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
                Text(
                    text = "Real-time index, crypto & commodity tracking",
                    fontSize = 12.sp,
                    color = MayaCyanNeon
                )
            }
        }

        // Live Feed Indicator
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0F1E33),
            border = BorderStroke(1.dp, MayaCyanNeon.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = MayaCyanNeon,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Market tracking enabled. Ask Maya e.g., 'What is Bitcoin trading at today?' for up-to-date audio quotes.",
                    fontSize = 11.sp,
                    color = MayaTextSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Market Cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            sampleMarkets.forEach { item ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = item.symbol,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaTextPrimary
                            )
                            Text(
                                text = item.name,
                                fontSize = 12.sp,
                                color = MayaTextSecondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = item.price,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaTextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (item.isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                    contentDescription = "Trend",
                                    tint = if (item.isPositive) MayaGreenNeon else MayaRedNeon,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = item.change,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (item.isPositive) MayaGreenNeon else MayaRedNeon
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
