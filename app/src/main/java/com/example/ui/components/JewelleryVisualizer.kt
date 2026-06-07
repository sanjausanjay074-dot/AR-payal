package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun JewelleryVisualizer(
    visualKey: String,
    modifier: Modifier = Modifier
) {
    // Elegant luxury dark obsidian card background
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF23272F), Color(0xFF14161B)),
                    radius = 450f
                )
            )
            .aspectRatio(1.2f)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f

            // Premium Metal Silver/Platinum Gradients
            val silverGradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFE2E8F0), // Platinum
                    Color(0xFF94A3B8), // Mid Silver
                    Color(0xFFF1F5F9), // Ice highlights
                    Color(0xFF64748B), // Shadow
                    Color(0xFFCBD5E1)  // Reflective edge
                ),
                start = Offset(0f, 0f),
                end = Offset(width, height)
            )

            val goldHighlight = Brush.radialGradient(
                colors = listOf(Color(0xFFFFDF79), Color(0xFFD4AF37), Color(0xFF8C6D13)),
                center = Offset(centerX, centerY),
                radius = 120f
            )

            val oxidizedGradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF7F8C8D), // Lead matte
                    Color(0xFF2C3E50), // Antique black
                    Color(0xFFBDC3C7), // High light
                    Color(0xFF34495E)  // Depth
                ),
                start = Offset(centerX - 100f, centerY - 100f),
                end = Offset(centerX + 100f, centerY + 100f)
            )

            when {
                // RINGS
                visualKey == "celtic_ring" -> {
                    // Ring loop
                    drawCircle(
                        brush = silverGradient,
                        radius = 80f,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 24f)
                    )
                    // Oxidized core line
                    drawCircle(
                        color = Color(0xFF1E293B),
                        radius = 80f,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 4f)
                    )
                    // Interlocking knot carvings in the cardinal directions
                    for (i in 0 until 4) {
                        val angle = (i * 90) * (Math.PI / 180.0)
                        val kX = centerX + (80 * cos(angle)).toFloat()
                        val kY = centerY + (80 * sin(angle)).toFloat()
                        drawCircle(
                            color = Color(0xFFCBD5E1),
                            radius = 14f,
                            center = Offset(kX, kY),
                            style = Stroke(width = 4f)
                        )
                        drawCircle(
                            color = Color(0xFF1E293B),
                            radius = 6f,
                            center = Offset(kX, kY),
                            style = Stroke(width = 2f)
                        )
                    }
                }

                visualKey == "eclipse_signet" -> {
                    // Signet shoulders
                    drawOval(
                        brush = silverGradient,
                        topLeft = Offset(centerX - 100f, centerY - 45f),
                        size = Size(200f, 90f)
                    )
                    // Flat top bezel
                    drawRect(
                        brush = silverGradient,
                        topLeft = Offset(centerX - 50f, centerY - 50f),
                        size = Size(100f, 100f)
                    )
                    // Deep black onyx center gem
                    drawRect(
                        color = Color(0xFF0F172A),
                        topLeft = Offset(centerX - 35f, centerY - 35f),
                        size = Size(70f, 70f)
                    )
                    // Gem facets
                    drawLine(
                        color = Color(0xFF475569),
                        start = Offset(centerX - 35f, centerY - 35f),
                        end = Offset(centerX + 35f, centerY + 35f),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color(0xFF475569),
                        start = Offset(centerX + 35f, centerY - 35f),
                        end = Offset(centerX - 35f, centerY + 35f),
                        strokeWidth = 2f
                    )
                }

                visualKey == "moonstone_solitaire" -> {
                    // Ring Band
                    drawCircle(
                        brush = silverGradient,
                        radius = 75f,
                        center = Offset(centerX, centerY + 20f),
                        style = Stroke(width = 12f)
                    )
                    // High crown basket
                    drawOval(
                        brush = silverGradient,
                        topLeft = Offset(centerX - 42f, centerY - 70f),
                        size = Size(84f, 84f)
                    )
                    // Iridescent Moonstone
                    val moonstoneBrush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE0F2FE), // Blue sky halo
                            Color(0xFFC084FC), // Violet amethyst fire
                            Color(0xFF38BDF8), // Turquoise spark
                            Color(0xFF93C5FD)  // Translucent milk
                        ),
                        center = Offset(centerX - 10f, centerY - 28f),
                        radius = 45f
                    )
                    drawCircle(
                        brush = moonstoneBrush,
                        radius = 35f,
                        center = Offset(centerX, centerY - 28f)
                    )
                    // Star sparkle indicators
                    drawSparkle(this, centerX + 40f, centerY - 50f)
                    drawSparkle(this, centerX - 45f, centerY - 15f)
                }

                // NECKLACES
                visualKey == "luna_necklace" -> {
                    // Draping chain
                    drawChainLoop(this, centerX, centerY - 60f, radiusY = 80f, radiusX = 110f)
                    
                    // Crescent moon pendant clasp
                    drawRect(
                        brush = silverGradient,
                        topLeft = Offset(centerX - 6f, centerY + 10f),
                        size = Size(12f, 15f)
                    )

                    // Moon curvature path
                    val moonCenter = Offset(centerX, centerY + 45f)
                    drawCircle(
                        brush = silverGradient,
                        radius = 36f,
                        center = moonCenter
                    )
                    drawCircle(
                        color = Color(0xFF14161B), // Subtract to create crescent
                        radius = 29f,
                        center = Offset(centerX - 12f, centerY + 45f)
                    )

                    // Inlaid Sapphire sparkling star
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White, Color(0xFF60A5FA), Color.Transparent),
                            radius = 18f,
                            center = Offset(centerX + 12f, centerY + 45f)
                        ),
                        radius = 12f,
                        center = Offset(centerX + 12f, centerY + 45f)
                    )
                }

                visualKey == "wing_choker" -> {
                    // Horizontal collar curve
                    val neckY = centerY - 10f
                    val step = 32f
                    for (i in -3..3) {
                        val offsetMultiplier = i.toFloat()
                        val linkX = centerX + (offsetMultiplier * step)
                        val linkY = neckY + (offsetMultiplier * offsetMultiplier * 4f)
                        
                        // Wing scales / feather patterns
                        withTransform({
                            rotate(degrees = offsetMultiplier * 10f, pivot = Offset(linkX, linkY))
                        }) {
                            drawOval(
                                brush = oxidizedGradient,
                                topLeft = Offset(linkX - 18f, linkY - 14f),
                                size = Size(36f, 52f)
                            )
                            drawOval(
                                brush = silverGradient,
                                topLeft = Offset(linkX - 14f, linkY - 10f),
                                size = Size(28f, 44f),
                                style = Stroke(width = 3f)
                            )
                        }
                    }
                }

                visualKey == "tree_locket" -> {
                    // Pendant bail
                    drawChainLoop(this, centerX, centerY - 75f, radiusY = 40f, radiusX = 70f)
                    drawOval(
                        brush = silverGradient,
                        topLeft = Offset(centerX - 8f, centerY - 45f),
                        size = Size(16f, 24f)
                    )
                    // High-rim circular locket housing
                    drawCircle(
                        brush = oxidizedGradient,
                        radius = 65f,
                        center = Offset(centerX, centerY + 15f)
                    )
                    drawCircle(
                        brush = silverGradient,
                        radius = 60f,
                        center = Offset(centerX, centerY + 15f),
                        style = Stroke(width = 6f)
                    )
                    // Delicate Tree of life branches etched in shiny gold finish
                    val treeBaseY = centerY + 55f
                    // Trunk
                    drawLine(
                        brush = goldHighlight,
                        start = Offset(centerX, treeBaseY),
                        end = Offset(centerX, centerY + 10f),
                        strokeWidth = 5f
                    )
                    // Branches
                    drawLine(
                        brush = goldHighlight,
                        start = Offset(centerX, centerY + 10f),
                        end = Offset(centerX - 35f, centerY - 15f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        brush = goldHighlight,
                        start = Offset(centerX, centerY + 10f),
                        end = Offset(centerX + 35f, centerY - 15f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        brush = goldHighlight,
                        start = Offset(centerX, centerY + 25f),
                        end = Offset(centerX - 25f, centerY + 10f),
                        strokeWidth = 2.5f
                    )
                    drawLine(
                        brush = goldHighlight,
                        start = Offset(centerX, centerY + 25f),
                        end = Offset(centerX + 25f, centerY + 10f),
                        strokeWidth = 2.5f
                    )
                }

                // PAYAL (ANKLETS)
                visualKey.startsWith("custom_payal:") || visualKey.contains("payal") -> {
                    val payalKey = if (visualKey.startsWith("custom_payal:")) {
                        visualKey
                    } else {
                        when (visualKey) {
                            "jodhpur_payal" -> "custom_payal:Heavy Chain:Oxidized:Ghungroo Bells:None"
                            "deccan_payal" -> "custom_payal:Delicate Link:Polished:Dangling Charms:Turquoise"
                            "ganga_payal" -> "custom_payal:Thick Cord:Oxidized:Ghungroo Bells:Garnet"
                            else -> "custom_payal:Delicate Link:Polished:Ghungroo Bells:None"
                        }
                    }
                    val parts = payalKey.split(":")
                    val pStyle = parts.getOrNull(1) ?: "Heavy Chain"
                    val pFinish = parts.getOrNull(2) ?: "Polished"
                    val pBells = parts.getOrNull(3) ?: "Ghungroo Bells"
                    val pGem = parts.getOrNull(4) ?: "None"

                    // Finish brush definition
                    val finishBrush = when (pFinish.lowercase()) {
                        "oxidized" -> oxidizedGradient
                        "brushed" -> Brush.sweepGradient(
                            colors = listOf(Color(0xFF94A3B8), Color(0xFFCBD5E1), Color(0xFF94A3B8)),
                            center = Offset(centerX, centerY)
                        )
                        "antique gold" -> Brush.linearGradient(
                            colors = listOf(Color(0xFFFCD34D), Color(0xFFD97706), Color(0xFFFBBF24)),
                            start = Offset(0f, 0f), end = Offset(width, height)
                        )
                        else -> silverGradient // Polished / default
                    }

                    // 1. Draw double-layered chain drape (higher chain)
                    val sWidth = when (pStyle.lowercase()) {
                        "heavy chain" -> 10f
                        "thick cord" -> 12f
                        "delicate link" -> 5f
                        else -> 8f
                    }
                    
                    // Higher primary loop arc
                    drawArc(
                        brush = finishBrush,
                        startAngle = 15f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = Offset(centerX - 130f, centerY - 100f),
                        size = Size(260f, 150f),
                        style = Stroke(width = sWidth, cap = StrokeCap.Round)
                    )

                    // Lower swinging loop arc
                    drawArc(
                        brush = finishBrush,
                        startAngle = 20f,
                        sweepAngle = 140f,
                        useCenter = false,
                        topLeft = Offset(centerX - 145f, centerY - 80f),
                        size = Size(290f, 160f),
                        style = Stroke(width = sWidth * 0.6f, cap = StrokeCap.Round)
                    )

                    // Draw decorative chain texture along both lines
                    val pointsCount = 12
                    val rx = 145f
                    val ry = 80f
                    val cy = centerY
                    for (i in 0..pointsCount) {
                        val progress = i.toFloat() / pointsCount
                        val angleRad = (25f + progress * 130f) * (Math.PI / 180.0)
                        val lx = centerX + rx * cos(angleRad).toFloat()
                        val ly = cy + ry * sin(angleRad).toFloat()
                        // Small chain details
                        drawCircle(
                            color = Color.White.copy(alpha = 0.5f),
                            radius = 3.5f,
                            center = Offset(lx, ly)
                        )
                    }

                    // 2. Hanging Ghungroo Bells or Charms spaced beautifully
                    val bellsCount = 5
                    for (i in 0 until bellsCount) {
                        val progress = (i.toFloat() + 0.5f) / bellsCount
                        val angleRad = (30f + progress * 120f) * (Math.PI / 180.0)
                        val bx = centerX + rx * cos(angleRad).toFloat()
                        val by = cy + ry * sin(angleRad).toFloat()

                        // Drop connection stick/loop
                        drawLine(
                            brush = finishBrush,
                            start = Offset(bx, by),
                            end = Offset(bx, by + 10f),
                            strokeWidth = 3f
                        )

                        if (pBells.lowercase().contains("ghungroo")) {
                            // Traditional spherical bell with chime slit
                            drawCircle(
                                brush = finishBrush,
                                radius = 9f,
                                center = Offset(bx, by + 18f)
                            )
                            drawCircle(
                                color = Color(0xFF1E293B),
                                radius = 2.5f,
                                center = Offset(bx, by + 23f)
                            )
                        } else if (pBells.lowercase().contains("charms")) {
                            // Beautiful dangling leaf charms and star diamonds
                            if (i % 2 == 0) {
                                // Leaf charm
                                drawOval(
                                    brush = finishBrush,
                                    topLeft = Offset(bx - 5f, by + 10f),
                                    size = Size(10f, 16f)
                                )
                            } else {
                                // Star charm
                                drawSparkle(this, bx, by + 15f)
                            }
                        }
                    }

                    // 3. Central Gemstone focal piece if selected
                    if (pGem != "None") {
                        val gemColor = when (pGem.lowercase()) {
                            "turquoise" -> Color(0xFF14B8A6)
                            "amethyst" -> Color(0xFFA855F7)
                            "onyx" -> Color(0xFF1E293B)
                            "garnet" -> Color(0xFFB91C1C)
                            else -> null
                        }
                        if (gemColor != null) {
                            // Center gem hangs directly from the middle bottom
                            val gbx = centerX
                            val gby = cy + ry + 10f
                            
                            drawCircle(
                                brush = finishBrush,
                                radius = 13f,
                                center = Offset(gbx, gby)
                            )
                            drawCircle(
                                color = gemColor,
                                radius = 9f,
                                center = Offset(gbx, gby)
                            )
                            drawSparkle(this, gbx + 5f, gby - 5f)
                        }
                    }
                }

                // BRACELETS (Standard Custom Visuals)
                visualKey.startsWith("custom:") || visualKey == "hammer_cuff" || visualKey == "viking_torque" -> {
                    // Extract specifications: custom:style:finish:gemstone:engraving
                    val parts = visualKey.split(":")
                    val bStyle = parts.getOrNull(1) ?: if (visualKey == "viking_torque") "Braided" else "Cuff"
                    val bFinish = parts.getOrNull(2) ?: if (visualKey == "hammer_cuff") "Hammered" else "Polished"
                    val bGem = parts.getOrNull(3) ?: if (visualKey == "hammer_cuff") "Turquoise" else "None"

                    // Finish brush definition
                    val finishBrush = when (bFinish.lowercase()) {
                        "oxidized" -> oxidizedGradient
                        "hammered" -> Brush.linearGradient(
                            colors = listOf(Color(0xFFE2E8F0), Color(0xFF64748B), Color(0xFFF1F5F9), Color(0xFF94A3B8)),
                            start = Offset(0f, centerY), end = Offset(width, centerY)
                        )
                        "brushed" -> Brush.sweepGradient(
                            colors = listOf(Color(0xFF94A3B8), Color(0xFFCBD5E1), Color(0xFF94A3B8)),
                            center = Offset(centerX, centerY)
                        )
                        else -> silverGradient // Polished / default
                    }

                    // Gemstone Colors
                    val gemColor = when (bGem.lowercase()) {
                        "turquoise" -> Color(0xFF14B8A6) // Deep beautiful ocean teal
                        "amethyst" -> Color(0xFFA855F7)  // Magical violet amethyst
                        "onyx" -> Color(0xFF1E293B)      // Mirror deep black obsidian
                        "garnet" -> Color(0xFFB91C1C)    // Rich dark blood red
                        "sterling bead" -> Color(0xFFE2E8F0) // Matching silver sphere
                        else -> null
                    }

                    if (bStyle.lowercase() == "braided" || bStyle.lowercase() == "torque") {
                        // Twist braided torque rings
                        drawCircle(
                            brush = finishBrush,
                            radius = 95f,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 16f)
                        )
                        drawCircle(
                            brush = if (bFinish.lowercase() == "oxidized") silverGradient else oxidizedGradient,
                            radius = 95f,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 8f)
                        )
                        // Braid helix indicators (spaced dots on circle)
                        for (i in 0 until 12) {
                            val angle = (i * 30f) * (Math.PI / 180.0)
                            val dotX = centerX + (95f * cos(angle)).toFloat()
                            val dotY = centerY + (95f * sin(angle)).toFloat()
                            drawCircle(
                                color = Color(0x99FFFFFF),
                                radius = 4f,
                                center = Offset(dotX, dotY)
                            )
                        }

                        // Wolf heads / Terminal Collars
                        drawCircle(
                            brush = silverGradient,
                            radius = 16f,
                            center = Offset(centerX - 40f, centerY + 85f)
                        )
                        drawCircle(
                            brush = silverGradient,
                            radius = 16f,
                            center = Offset(centerX + 40f, centerY + 85f)
                        )
                    } else if (bStyle.lowercase() == "chain") {
                        // Drawing heavy luxury silver links
                        val stepY = 22f
                        for (i in -3..3) {
                            val index = i.toFloat()
                            val linkY = centerY + (index * stepY)
                            drawRoundRect(
                                brush = finishBrush,
                                topLeft = Offset(centerX - 42f + (if (i % 2 == 0) -8f else 8f), linkY - 14f),
                                size = Size(84f, 28f),
                                cornerRadius = CornerRadius(14f, 14f),
                                style = Stroke(width = 6f)
                            )
                        }
                    } else if (bStyle.lowercase() == "bangle") {
                        // Perfect continuous thin circular hoop with high polish
                        drawCircle(
                            brush = finishBrush,
                            radius = 100f,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 10f)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.6f),
                            radius = 101f,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 2f)
                        )
                    } else {
                        // CUFF (Open front solid band)
                        drawArc(
                            brush = finishBrush,
                            startAngle = -215f,
                            sweepAngle = 250f,
                            useCenter = false,
                            topLeft = Offset(centerX - 100f, centerY - 100f),
                            size = Size(200f, 200f),
                            style = Stroke(width = 28f, cap = StrokeCap.Round)
                        )

                        // If Hammered: overlay overlapping soft hammered dimple textures
                        if (bFinish.lowercase() == "hammered") {
                            for (i in 0 until 12) {
                                val tAngle = (-210f + (i * 20f)) * (Math.PI / 180.0)
                                val hX = centerX + (100f * cos(tAngle)).toFloat()
                                val hY = centerY + (100f * sin(tAngle)).toFloat()
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.18f),
                                    radius = 12f,
                                    center = Offset(hX, hY)
                                )
                            }
                        }
                    }

                    // Mounted gemstone bezel in center/head
                    if (gemColor != null) {
                        val bezelY = centerY - 95f
                        // Bezel rim
                        drawCircle(
                            brush = silverGradient,
                            radius = 24f,
                            center = Offset(centerX, bezelY)
                        )
                        // Gem highlight
                        drawCircle(
                            color = gemColor,
                            radius = 18f,
                            center = Offset(centerX, bezelY)
                        )
                        // Star sparkle on gem
                        drawSparkle(this, centerX + 12f, bezelY - 12f)
                    }
                }

                else -> {
                    // Default Fallback: Simple silver crest medallion
                    drawCircle(
                        brush = silverGradient,
                        radius = 80f,
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = Color(0xFF1E293B),
                        radius = 70f,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 4f)
                    )
                }
            }
        }
    }
}

// Helpers for drawings
private fun drawSparkle(scope: DrawScope, x: Float, y: Float) {
    val size = 12f
    scope.drawLine(
        color = Color.White,
        start = Offset(x - size, y),
        end = Offset(x + size, y),
        strokeWidth = 3f
    )
    scope.drawLine(
        color = Color.White,
        start = Offset(x, y - size),
        end = Offset(x, y + size),
        strokeWidth = 3f
    )
    scope.drawCircle(
        color = Color.White,
        radius = 4f,
        center = Offset(x, y)
    )
}

private fun drawChainLoop(scope: DrawScope, centerX: Float, centerY: Float, radiusY: Float, radiusX: Float) {
    scope.drawOval(
        color = Color(0xFF475569),
        topLeft = Offset(centerX - radiusX, centerY - radiusY),
        size = Size(radiusX * 2f, radiusY * 2f),
        style = Stroke(width = 3f)
    )
    // Small dots indicating jewelry chain links
    val segments = 24
    for (i in 0 until segments) {
        val angle = (i * (360f / segments)) * (Math.PI / 180.0)
        val lX = centerX + (radiusX * cos(angle)).toFloat()
        val lY = centerY + (radiusY * sin(angle)).toFloat()
        scope.drawCircle(
            color = Color(0xFF64748B),
            radius = 3.5f,
            center = Offset(lX, lY)
        )
    }
}
