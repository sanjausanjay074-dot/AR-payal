package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.CartEntity
import com.example.data.local.CommunityListingEntity
import com.example.data.local.CustomDesignEntity
import com.example.data.local.WishlistEntity
import com.example.data.model.JewelleryCatalog
import com.example.data.model.JewelleryProduct
import com.example.ui.components.JewelleryVisualizer
import com.example.ui.viewmodel.AiDesignState
import com.example.ui.viewmodel.JewelleryViewModel
import java.text.DecimalFormat

private val CurrencyFormatter = DecimalFormat("$#,##0.00")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainBoutiqueLayout(
    viewModel: JewelleryViewModel,
    modifier: Modifier = Modifier
) {
    val currentSection by viewModel.currentSection.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val wishlistItems by viewModel.wishlistItems.collectAsStateWithLifecycle()
    val notificationMsg by viewModel.notificationMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Selected product for detailing dialog
    var detailedProduct by remember { mutableStateOf<JewelleryProduct?>(null) }
    var detailedCustom by remember { mutableStateOf<CustomDesignEntity?>(null) }
    var detailedCommunity by remember { mutableStateOf<CommunityListingEntity?>(null) }

    // Coupon discount simulator
    var discountCodeApplied by remember { mutableStateOf(false) }
    var couponText by remember { mutableStateOf("") }

    // Checkout show dialog
    var showCheckoutSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(notificationMsg) {
        notificationMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearNotification()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Argent Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "A R G E N T U M",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 4.sp
                        )
                    }

                    // Quick Cart Icon Bubble
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable { viewModel.navigateTo("cart") }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Shopping Bag",
                            tint = if (cartItems.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                        if (cartItems.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cartItems.sumOf { it.quantity }.toString(),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Elegant luxury Professional Polish bottom navigation bar
            Box(
                modifier = Modifier
                    .background(Color(0xFFF0F1F9))
                    .navigationBarsPadding()
            ) {
                NavigationBar(
                    containerColor = Color(0xFFF0F1F9),
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    val items = listOf(
                        Triple("catalog", "Boutique", Icons.Default.Home),
                        Triple("customizer", "Custom Studio", Icons.Default.Search),
                        Triple("wishlist", "Favorites", Icons.Default.Favorite),
                        Triple("cart", "My Bag", Icons.Default.ShoppingCart)
                    )

                    items.forEach { (section, label, icon) ->
                        val selected = currentSection == section
                        NavigationBarItem(
                            selected = selected,
                            onClick = { viewModel.navigateTo(section) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFFD1E4FF),
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.secondary,
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.testTag("nav_tab_$section")
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentSection,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "section_crossfade"
            ) { section ->
                when (section) {
                    "catalog" -> CatalogScreen(
                        viewModel = viewModel,
                        onProductClick = { detailedProduct = it },
                        onCommunityClick = { detailedCommunity = it }
                    )
                    "customizer" -> CustomizerScreen(
                        viewModel = viewModel,
                        onCustomProductClick = { detailedCustom = it }
                    )
                    "wishlist" -> WishlistScreen(
                        viewModel = viewModel,
                        onProductClick = { detailedProduct = it }
                    )
                    "cart" -> CartScreen(
                        viewModel = viewModel,
                        discountApplied = discountCodeApplied,
                        couponText = couponText,
                        onCouponChange = { couponText = it },
                        onApplyCoupon = {
                            if (couponText.trim().uppercase() == "SILVER925") {
                                discountCodeApplied = true
                            }
                        },
                        onCheckout = {
                            showCheckoutSuccess = true
                        }
                    )
                }
            }
        }
    }

    // Modal Details Dialog for Standard Boutique Items
    detailedProduct?.let { product ->
        val isFav = viewModel.isItemWishlisted(product.id, product.title).collectAsStateWithLifecycle(false)
        ProductDetailDialog(
            viewModel = viewModel,
            product = product,
            isWishlisted = isFav.value,
            onDismiss = { detailedProduct = null },
            onToggleWishlist = {
                viewModel.toggleWishlist(product.id, product.title, product.category, product.price, product.visualKey)
            },
            onAddToCart = {
                viewModel.addToCart(product.id, product.title, product.category, product.price, product.visualKey)
                detailedProduct = null
            }
        )
    }

    // Modal Details Dialog for saved custom designs
    detailedCustom?.let { design ->
        CustomDesignDetailDialog(
            design = design,
            onDismiss = { detailedCustom = null },
            onDelete = {
                viewModel.deleteCustomDesign(design.id)
                detailedCustom = null
            },
            onAddToCart = {
                val categoryName = if (design.style.startsWith("Payal:")) "Custom Payal" else "Custom Bracelet"
                val imgSpec = if (design.style.startsWith("Payal:")) {
                    val actStyle = design.style.substringAfter("Payal:")
                    "custom_payal:$actStyle:${design.finish}:${design.engraving}:${design.gemstone}"
                } else {
                    "custom:${design.style}:${design.finish}:${design.gemstone}:${design.engraving}"
                }
                val metadata = if (design.style.startsWith("Payal:")) {
                    val actStyle = design.style.substringAfter("Payal:")
                    "Style: $actStyle | Finish: ${design.finish} | Bells: ${design.engraving} | Gem: ${design.gemstone}"
                } else {
                    "Style: ${design.style} | Finish: ${design.finish} | Gem: ${design.gemstone} | Engraved: '${design.engraving}'"
                }
                viewModel.addToCart(-5, design.title, categoryName, design.price, imgSpec, metadata)
                detailedCustom = null
            }
        )
    }

    // Modal Details Dialog for community posted listings
    detailedCommunity?.let { listing ->
        CommunityListingDetailDialog(
            listing = listing,
            onDismiss = { detailedCommunity = null },
            onDelete = {
                viewModel.removePayalFromSale(listing)
                detailedCommunity = null
            },
            onAddToCart = {
                val metadata = "Seller: ${listing.sellerName} | Contact: ${listing.contactInfo} | Specs: ${listing.bellsType}, ${listing.purity}"
                viewModel.addToCart(-2, listing.title, "Payal", listing.price, listing.imageSpec, metadata)
                detailedCommunity = null
            }
        )
    }

    // Checkout Order animation and confirm dialog
    if (showCheckoutSuccess) {
        Dialog(onDismissRequest = {
            showCheckoutSuccess = false
            viewModel.checkout()
        }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2126)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFF475569)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "ORDER COMMITTED",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFFE2E8F0),
                        textAlign = TextAlign.Center,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "Our master silversmiths are preparing your bespoke order.\n\nA confirmation receipt with shipment tracking details has been sent to your email.",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Button(
                        onClick = {
                            showCheckoutSuccess = false
                            viewModel.checkout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .testTag("dismiss_checkout_success")
                    ) {
                        Text("Accept & Return", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ------------------- BOUTIQUE CATALOG SCREEN -------------------
@Composable
fun CatalogScreen(
    viewModel: JewelleryViewModel,
    onProductClick: (JewelleryProduct) -> Unit,
    onCommunityClick: (CommunityListingEntity) -> Unit
) {
    val selectedCat by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val wishlistItems by viewModel.wishlistItems.collectAsStateWithLifecycle()
    val communityListings by viewModel.communityListings.collectAsStateWithLifecycle()

    var subMode by remember { mutableStateOf("Boutique") } // "Boutique" or "Marketplace"
    var showPostDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Payal", "Ring", "Necklace", "Bracelet")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Hero Section Promo Banner - Professional Polish Style
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.Info, 
                        contentDescription = "Promo Info", 
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), 
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "SUMMER COMMISSIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        letterSpacing = 1.5.sp
                    )
                }
                Text(
                    text = "925 Sterling Masterworks",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Browse master-made collections or head to the Community Bazaar to post and buy bespoke silver Payals instantly.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Sub-Mode Section Switcher: Boutique vs Community Marketplace
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE1E2EC))
                .border(1.dp, Color(0xFFC4C6D0).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
        ) {
            listOf("Boutique Collection", "Community Marketplace").forEach { mode ->
                val active = (subMode == "Boutique" && mode.startsWith("Boutique")) || 
                             (subMode == "Marketplace" && mode.startsWith("Community"))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (active) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { subMode = if (mode.startsWith("Boutique")) "Boutique" else "Marketplace" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode,
                        color = if (active) Color.White else MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (subMode == "Boutique") {
            // Horizontal Category Tab Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val active = selectedCat == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (active) MaterialTheme.colorScheme.primary else Color(0xFFE1E2EC))
                            .border(1.dp, if (active) MaterialTheme.colorScheme.primary else Color(0xFFC4C6D0).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { viewModel.updateCategory(cat) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            color = if (active) Color.White else MaterialTheme.colorScheme.secondary,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Curated Catalogue Grid
            val filteredProducts = remember(selectedCat) {
                if (selectedCat == "All") {
                    JewelleryCatalog.items
                } else {
                    JewelleryCatalog.items.filter { it.category == selectedCat }
                }
            }

            if (filteredProducts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Empty",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Items in Category",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Check back soon as our artisans reveal new collections.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 8.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        val isFav = wishlistItems.any { it.productId == product.id }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFC4C6D0).copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProductClick(product) }
                        ) {
                            Column {
                                // Canvas Image placeholder
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    JewelleryVisualizer(
                                        visualKey = product.visualKey,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // Heart favorite float bubble
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(10.dp)
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.9f))
                                            .border(1.dp, Color(0xFFC4C6D0).copy(alpha = 0.3f), CircleShape)
                                            .clickable {
                                                viewModel.toggleWishlist(
                                                    product.id,
                                                    product.title,
                                                    product.category,
                                                    product.price,
                                                    product.visualKey
                                                )
                                            }
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite",
                                            tint = if (isFav) Color(0xFFEF4444) else MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                // Texts
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = product.category.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = product.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${product.silverWeight}g • ${product.purity}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = CurrencyFormatter.format(product.price),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        // Add to Cart small button
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primary)
                                                .clickable {
                                                    viewModel.addToCart(
                                                        product.id,
                                                        product.title,
                                                        product.category,
                                                        product.price,
                                                        product.visualKey
                                                    )
                                                }
                                                .padding(6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Add",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // COMMUNITY MARKETPLACE SCREEN
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Sell Anklets CTA Bar - Professional Polish style
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Have custom Payals or fine silver anklets to sell?",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { showPostDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(50).also { /* pill shaped button */ },
                            modifier = Modifier.testTag("post_payal_for_sale_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Item", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Post Your Payal for Sale", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // Scrollable community grid
                if (communityListings.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "No local listings",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Community Listings Yet",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Be the first silver smith to post a beautiful custom Payal for sale here!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp)
                        )
                    }
                } else {
                    Text(
                        text = "LIVE BAZAAR FEED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(communityListings, key = { it.id }) { listing ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFC4C6D0).copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCommunityClick(listing) }
                            ) {
                                Column {
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        JewelleryVisualizer(
                                            visualKey = listing.imageSpec,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(6.dp)
                                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "Seller: ${listing.sellerName}",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "PAYAL",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = listing.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${listing.silverWeight}g • ${listing.purity}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = CurrencyFormatter.format(listing.price),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )

                                            // Quick buy button
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary)
                                                    .clickable {
                                                        val metadata = "Seller: ${listing.sellerName} | Contact: ${listing.contactInfo} | Specs: ${listing.bellsType}, ${listing.purity}"
                                                        viewModel.addToCart(
                                                            -2,
                                                            listing.title,
                                                            "Payal",
                                                            listing.price,
                                                            listing.imageSpec,
                                                            metadata
                                                        )
                                                    }
                                                    .padding(6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ShoppingCart,
                                                    contentDescription = "Buy",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
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
        }
    }

    if (showPostDialog) {
        PostPayalDialog(
            onDismiss = { showPostDialog = false },
            onSubmit = { title, desc, price, weight, bells, purity, contact, seller, spec ->
                viewModel.postPayalForSale(title, desc, price, weight, bells, purity, contact, seller, spec)
                showPostDialog = false
            }
        )
    }
}

// ------------------- COMMUNITY LISTING DIALOGS & FORMS -------------------
@Composable
fun PostPayalDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, description: String, price: Double, silverWeight: Double, bellsType: String, purity: String, contactInfo: String, sellerName: String, imageSpec: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var weightStr by remember { mutableStateOf("") }
    var purity by remember { mutableStateOf("925 Pure Silver") }
    
    // Spec properties for drawing
    var chainStyle by remember { mutableStateOf("Heavy Chain") }
    var finish by remember { mutableStateOf("Polished") }
    var bells by remember { mutableStateOf("Ghungroo Bells") }
    var gemstone by remember { mutableStateOf("None") }
    
    var sellerName by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }

    val purities = listOf("925 Pure Silver", "99% Fine Silver", "Mixed Silver Alloy")
    val chainStyles = listOf("Heavy Chain", "Thick Cord", "Delicate Link", "Traditional Bangle")
    val finishes = listOf("Polished", "Brushed", "Oxidized", "Antique Gold")
    val bellsTypes = listOf("Ghungroo Bells", "Dangling Charms", "None/Plain")
    val gemstones = listOf("None", "Turquoise", "Amethyst", "Onyx", "Garnet")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14161B)),
            border = BorderStroke(1.dp, Color(0xFF2E343E)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "POST YOUR PAYAL",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Text(
                    text = "Fill in the details below to list your handcrafted, custom, or pre-loved silver Payal in our boutique community showcase.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 15.sp
                )

                // Input fields
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Product Title (e.g., Bridal Royal Ghungroo)") },
                    textStyle = TextStyle(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFA5C8E4),
                        unfocusedBorderColor = Color(0xFF2E343E)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_listing_title")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Artisan Story") },
                    textStyle = TextStyle(color = Color.White),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFA5C8E4),
                        unfocusedBorderColor = Color(0xFF2E343E)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_listing_description")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Price ($)") },
                        textStyle = TextStyle(color = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFA5C8E4),
                            unfocusedBorderColor = Color(0xFF2E343E)
                        ),
                        modifier = Modifier.weight(1f).testTag("add_listing_price")
                    )
                    OutlinedTextField(
                        value = weightStr,
                        onValueChange = { weightStr = it },
                        label = { Text("Weight (g)") },
                        textStyle = TextStyle(color = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFA5C8E4),
                            unfocusedBorderColor = Color(0xFF2E343E)
                        ),
                        modifier = Modifier.weight(1f).testTag("add_listing_weight")
                    )
                }

                // Custom specs to draw the Payal automatically on canvas
                Text("Anklet Visual Specs (Custom Canvas Maker):", fontSize = 12.sp, color = Color(0xFFA5C8E4), fontWeight = FontWeight.Bold)

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Purity
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Silver Purity", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            purities.forEach { p ->
                                val active = purity == p
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (active) Color(0xFFA5C8E4) else Color(0xFF2E343E))
                                        .clickable { purity = p }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(p, color = if (active) Color.Black else Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Weaving/Chain Style
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Weaving Chain Type", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            chainStyles.forEach { cs ->
                                val active = chainStyle == cs
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (active) Color(0xFFA5C8E4) else Color(0xFF2E343E))
                                        .clickable { chainStyle = cs }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(cs, color = if (active) Color.Black else Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Finish
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Silver Finish", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            finishes.forEach { f ->
                                val active = finish == f
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (active) Color(0xFFA5C8E4) else Color(0xFF2E343E))
                                        .clickable { finish = f }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(f, color = if (active) Color.Black else Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Bells Type
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Chiming Bells / Charms", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            bellsTypes.forEach { bt ->
                                val active = bells == bt
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (active) Color(0xFFA5C8E4) else Color(0xFF2E343E))
                                        .clickable { bells = bt }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(bt, color = if (active) Color.Black else Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Gemstone Accent
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Gemstone Accent", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            gemstones.forEach { g ->
                                val active = gemstone == g
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (active) Color(0xFFA5C8E4) else Color(0xFF2E343E))
                                        .clickable { gemstone = g }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(g, color = if (active) Color.Black else Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // Seller Credentials (Luxury Marketplace requires validation/contact detail)
                Text("Seller Credentials:", fontSize = 12.sp, color = Color(0xFFA5C8E4), fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = sellerName,
                    onValueChange = { sellerName = it },
                    label = { Text("Your Name (Seller)") },
                    textStyle = TextStyle(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFA5C8E4),
                        unfocusedBorderColor = Color(0xFF2E343E)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_listing_seller_name")
                )

                OutlinedTextField(
                    value = contactInfo,
                    onValueChange = { contactInfo = it },
                    label = { Text("Contact Info (Phone / Email)") },
                    textStyle = TextStyle(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFA5C8E4),
                        unfocusedBorderColor = Color(0xFF2E343E)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_listing_contact")
                )

                // Live drawing preview directly inside the list form!
                Text("Your Payal Preview Card:", fontSize = 11.sp, color = Color(0xFF64748B))
                val tempVisualKey = "custom_payal:$chainStyle:$finish:$bells:$gemstone"
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF0F1113), RoundedCornerShape(12.dp)).padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    JewelleryVisualizer(
                        visualKey = tempVisualKey,
                        modifier = Modifier.size(130.dp)
                    )
                }

                Button(
                    onClick = {
                        val p = priceStr.toDoubleOrNull() ?: 0.0
                        val w = weightStr.toDoubleOrNull() ?: 0.0
                        onSubmit(title, description, p, w, bells, purity, contactInfo, sellerName, tempVisualKey)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCBD5E1)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).testTag("submit_community_listing")
                ) {
                    Text("Publish to Silver Bazaar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CommunityListingDetailDialog(
    listing: CommunityListingEntity,
    onDismiss: () -> Unit,
    onAddToCart: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14161B)),
            border = BorderStroke(1.dp, Color(0xFF2E343E)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COMMUNITY BAZAAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA5C8E4),
                        letterSpacing = 1.5.sp
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth().height(160.dp).background(Color(0xFF0F1113), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    JewelleryVisualizer(
                        visualKey = listing.imageSpec,
                        modifier = Modifier.size(140.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = listing.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Serif
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SpecsChip("Purity", listing.purity)
                        SpecsChip("Weight", "${listing.silverWeight}g")
                        SpecsChip("Bells / Charms", listing.bellsType, fillPrimary = listing.bellsType != "None/Plain")
                    }
                }

                HorizontalDivider(color = Color(0xFF2E343E))

                Text(
                    text = listing.description,
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                HorizontalDivider(color = Color(0xFF2E343E))

                // Seller Detail card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2126)),
                    border = BorderStroke(1.dp, Color(0xFF2E343E)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Seller Information", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA5C8E4))
                        Text("Lister: ${listing.sellerName}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text("Contact Details: ${listing.contactInfo}", fontSize = 13.sp, color = Color(0xFF94A3B8))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = CurrencyFormatter.format(listing.price),
                        color = Color(0xFFA5C8E4),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (onDelete != null) {
                            IconButton(onClick = onDelete) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove listing", tint = Color(0xFFEF4444))
                            }
                        }

                        Button(
                            onClick = onAddToCart,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCBD5E1)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Add to cart", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Buy / Bag It", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ------------------- PRODUCT DETAIL MODAL DIALOG -------------------
@Composable
fun ProductDetailDialog(
    viewModel: JewelleryViewModel,
    product: JewelleryProduct,
    isWishlisted: Boolean,
    onDismiss: () -> Unit,
    onToggleWishlist: () -> Unit,
    onAddToCart: () -> Unit
) {
    val reviews by viewModel.getReviewsForProduct(product.id).collectAsStateWithLifecycle(initialValue = emptyList())

    // Review form inputs
    var userRating by remember { mutableStateOf(5) }
    var reviewerName by remember { mutableStateOf("") }
    var reviewerComment by remember { mutableStateOf("") }

    val averageRating = if (reviews.isNotEmpty()) {
        reviews.map { it.rating }.average()
    } else {
        5.0
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Details Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.category.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.5.sp
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.secondary)
                    }
                }

                // Render dynamic visualizer
                JewelleryVisualizer(
                    visualKey = product.visualKey,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.title,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )

                    // Average Rating Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (reviews.isNotEmpty()) {
                                "${String.format("%.1f", averageRating)} (${reviews.size})"
                            } else {
                                "5.0 (0)"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SpecsChip("Weight", "${product.silverWeight}g")
                    SpecsChip("Metal Purity", product.purity)
                }

                if (product.gemstone != "None") {
                    SpecsChip("Inlaid Gemstone", product.gemstone, fillPrimary = true)
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Description", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Text(
                        text = product.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )
                }

                if (product.artisanNotes.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("Artisan Silversmith Notes", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text(text = product.artisanNotes, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), lineHeight = 16.sp)
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // List of reviews
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Customer Appraisals",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (reviews.isEmpty()) {
                        Text(
                            text = "No appraisals yet. Be the first to share your thoughts on this masterpiece!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            reviews.forEach { rev ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f))
                                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                        .padding(10.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = rev.author,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                repeat(5) { index ->
                                                    Icon(
                                                        imageVector = Icons.Default.Star,
                                                        contentDescription = null,
                                                        tint = if (index < rev.rating) Color(0xFFFFB300) else Color(0xFFE1E2EC),
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = rev.comment,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // Submit appraisal form
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Submit an Appraisal",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Your Rating:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(5) { index ->
                                val starValue = index + 1
                                IconButton(
                                    onClick = { userRating = starValue },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Rate $starValue stars",
                                        tint = if (starValue <= userRating) Color(0xFFFFB300) else Color(0xFFE1E2EC),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = reviewerName,
                        onValueChange = { reviewerName = it },
                        label = { Text("Your Name") },
                        placeholder = { Text("e.g. John Doe") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("review_author_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    OutlinedTextField(
                        value = reviewerComment,
                        onValueChange = { reviewerComment = it },
                        label = { Text("Your Thoughts (Spaciously describe the artisan craft)") },
                        placeholder = { Text("Review description...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 60.dp)
                            .testTag("review_comment_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    val canSubmit = reviewerName.isNotBlank() && reviewerComment.isNotBlank()
                    Button(
                        onClick = {
                            if (canSubmit) {
                                viewModel.addReview(product.id, userRating, reviewerName, reviewerComment)
                                reviewerName = ""
                                reviewerComment = ""
                                userRating = 5
                            }
                        },
                        enabled = canSubmit,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("review_submit_button")
                    ) {
                        Text(
                            text = "Publish Review",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (canSubmit) Color.White else MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Suggested Price", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                        Text(
                            text = CurrencyFormatter.format(product.price),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onToggleWishlist,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            contentPadding = PaddingValues(10.dp)
                        ) {
                            Icon(
                                imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite Toggle",
                                tint = if (isWishlisted) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = onAddToCart,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("modal_add_to_cart")
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Add", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add to Bag", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpecsChip(label: String, value: String, fillPrimary: Boolean = false) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (fillPrimary) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            .border(1.dp, if (fillPrimary) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column {
            Text(label.uppercase(), fontSize = 8.sp, color = if (fillPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ------------------- ARTISAN CUSTOMIZER STUDIO -------------------
@Composable
fun CustomizerScreen(
    viewModel: JewelleryViewModel,
    onCustomProductClick: (CustomDesignEntity) -> Unit
) {
    val aiState by viewModel.aiDesignState.collectAsStateWithLifecycle()
    val savedDesigns by viewModel.customDesigns.collectAsStateWithLifecycle()

    var userDraftPrompt by remember { mutableStateOf("") }
    
    // Choose product type to customize
    var customizeType by remember { mutableStateOf("Bracelet") } // "Bracelet" or "Payal"
    
    // Manual inputs
    var activeStyle by remember { mutableStateOf("Cuff") }
    var activeFinish by remember { mutableStateOf("Polished") }
    var activeGem by remember { mutableStateOf("Turquoise") }
    var bellsType by remember { mutableStateOf("Ghungroo Bells") }
    var engravingText by remember { mutableStateOf("") }
    var customDesignName by remember { mutableStateOf("Aura Silver Bracelet") }

    // When product type changes, pre-populate default labels and options
    LaunchedEffect(customizeType) {
        if (customizeType == "Bracelet") {
            customDesignName = "Aura Silver Bracelet"
            activeStyle = "Cuff"
            activeGem = "Turquoise"
        } else {
            customDesignName = "Dharini Traditional Payal"
            activeStyle = "Heavy Chain"
            activeGem = "None"
        }
    }

    val styles = remember(customizeType) {
        if (customizeType == "Bracelet") {
            listOf("Cuff", "Chain", "Braided", "Bangle")
        } else {
            listOf("Heavy Chain", "Thick Cord", "Delicate Link", "Traditional Bangle")
        }
    }

    val finishes = remember(customizeType) {
        if (customizeType == "Bracelet") {
            listOf("Polished", "Oxidized", "Hammered", "Brushed")
        } else {
            listOf("Polished", "Brushed", "Oxidized", "Antique Gold")
        }
    }

    val gemstones = remember(customizeType) {
        if (customizeType == "Bracelet") {
            listOf("Turquoise", "Amethyst", "Onyx", "Garnet", "Sterling Bead", "None")
        } else {
            listOf("None", "Turquoise", "Amethyst", "Onyx", "Garnet")
        }
    }

    val bellsTypes = listOf("Ghungroo Bells", "Dangling Charms", "None/Plain")

    val estimatedPrice = remember(customizeType, activeStyle, activeFinish, activeGem, bellsType) {
        var base = if (customizeType == "Bracelet") 140.0 else 125.0
        if (customizeType == "Bracelet") {
            if (activeStyle == "Braided") base += 35.0
            if (activeStyle == "Chain") base += 25.0
            if (activeFinish == "Hammered") base += 15.0
            if (activeFinish == "Oxidized") base += 10.0
            if (activeGem != "None") base += 20.0
        } else {
            if (activeStyle == "Heavy Chain") base += 45.0
            if (activeStyle == "Traditional Bangle") base += 35.0
            if (activeFinish == "Antique Gold") base += 20.0
            if (activeFinish == "Oxidized") base += 10.0
            if (activeGem != "None") base += 15.0
            if (bellsType == "Ghungroo Bells") base += 15.0
            if (bellsType == "Dangling Charms") base += 25.0
        }
        base
    }

    val estimatedWeight = remember(customizeType, activeStyle) {
        if (customizeType == "Bracelet") {
            when (activeStyle) {
                "Braided" -> 22.4
                "Chain" -> 16.8
                "Bangle" -> 14.5
                else -> 19.5 // Cuff
            }
        } else {
            when (activeStyle) {
                "Heavy Chain" -> 28.5
                "Thick Cord" -> 15.2
                "Traditional Bangle" -> 22.0
                else -> 18.0 // Delicate Link
            }
        }
    }

    val currentVisualKey = remember(customizeType, activeStyle, activeFinish, activeGem, engravingText, bellsType) {
        if (customizeType == "Bracelet") {
            "custom:$activeStyle:$activeFinish:$activeGem:$engravingText"
        } else {
            "custom_payal:$activeStyle:$activeFinish:$bellsType:$activeGem"
        }
    }

    val kbd = LocalSoftwareKeyboardController.current
    val focus = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Decorative Heading - Professional Polish Style
        Column {
            Text(
                text = "ARTISAN DESIGN STUDIO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 2.sp
            )
            Text(
                text = "Sculpt Your Vision",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Segmented Switcher for Bracelet vs Payal
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE1E2EC))
                .border(1.dp, Color(0xFFC4C6D0).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
        ) {
            listOf("Customize Bracelet", "Customize Payal (Anklet)").forEach { type ->
                val active = (customizeType == "Bracelet" && type.contains("Bracelet")) || 
                             (customizeType == "Payal" && type.contains("Payal"))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (active) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { customizeType = if (type.contains("Bracelet")) "Bracelet" else "Payal" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = type,
                        color = if (active) Color.White else MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Live Custom Preview Canvas
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFC4C6D0).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FF), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    JewelleryVisualizer(
                        visualKey = currentVisualKey,
                        modifier = Modifier.size(170.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text("Custom Piece Title", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                        BasicTextField(
                            value = customDesignName,
                            onValueChange = { customDesignName = it },
                            textStyle = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                if (customizeType == "Bracelet") {
                                    viewModel.saveCustomBracelet(
                                        title = customDesignName,
                                        style = activeStyle,
                                        finish = activeFinish,
                                        gemstone = activeGem,
                                        engraving = engravingText,
                                        price = estimatedPrice,
                                        weight = estimatedWeight
                                    )
                                } else {
                                    viewModel.saveCustomBracelet(
                                        title = customDesignName,
                                        style = "Payal:$activeStyle",
                                        finish = activeFinish,
                                        gemstone = activeGem,
                                        engraving = bellsType,
                                        price = estimatedPrice,
                                        weight = estimatedWeight
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("save_custom_bracelet")
                        ) {
                            Text("Save Sketch", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        if (customizeType == "Payal") {
                            var showDirectPostDialog by remember { mutableStateOf(false) }

                            Button(
                                onClick = { showDirectPostDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.testTag("direct_list_payal_button")
                            ) {
                                Text("Sell This", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            if (showDirectPostDialog) {
                                PostPayalDialog(
                                    onDismiss = { showDirectPostDialog = false },
                                    onSubmit = { title, desc, price, weight, bType, purity, contact, seller, spec ->
                                        viewModel.postPayalForSale(title, desc, price, weight, bType, purity, contact, seller, spec)
                                        showDirectPostDialog = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // DESIGN CONTROLS SECTION - Professional Polish Style
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFC4C6D0).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Bespoke Controls",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp
                )

                // Style Controls
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val styleLabel = if (customizeType == "Bracelet") "Bracelet Base Style" else "Anklet Weaving Chain Style"
                    Text(styleLabel, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        styles.forEach { style ->
                            val active = activeStyle == style
                            FilterChip(
                                selected = active,
                                onClick = { activeStyle = style },
                                label = { Text(style, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFE1E2EC),
                                    labelColor = MaterialTheme.colorScheme.secondary
                                )
                            )
                        }
                    }
                }

                // Finish Controls
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Silver Finish Option", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        finishes.forEach { fin ->
                            val active = activeFinish == fin
                            FilterChip(
                                selected = active,
                                onClick = { activeFinish = fin },
                                label = { Text(fin, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFE1E2EC),
                                    labelColor = MaterialTheme.colorScheme.secondary
                                )
                            )
                        }
                    }
                }

                // Gemstone Controls
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Primary Gemstone Accent", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        gemstones.forEach { gem ->
                            val active = activeGem == gem
                            FilterChip(
                                selected = active,
                                onClick = { activeGem = gem },
                                label = { Text(gem, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFE1E2EC),
                                    labelColor = MaterialTheme.colorScheme.secondary
                                )
                            )
                        }
                    }
                }

                if (customizeType == "Payal") {
                    // Bells Option Block specifically for Payals
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Chiming Bells & Charms Style", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            bellsTypes.forEach { bt ->
                                val active = bellsType == bt
                                FilterChip(
                                    selected = active,
                                    onClick = { bellsType = bt },
                                    label = { Text(bt, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFFE1E2EC),
                                        labelColor = MaterialTheme.colorScheme.secondary
                                    )
                                )
                            }
                        }
                    }
                } else {
                    // Custom Engraving Text Field (Bracelets)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Hand-Carved Engraving (Max 24 chars)", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = engravingText,
                            onValueChange = { if (it.length <= 24) engravingText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("engraving_input"),
                            placeholder = { Text("e.g. FOREVER WITH YOU", color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color(0xFFC4C6D0),
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            ),
                            singleLine = true
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFC4C6D0).copy(alpha = 0.5f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Est. Silver Weight", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                        Text("${estimatedWeight}g Sterling 925", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Bespoke Pricing", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                        Text(CurrencyFormatter.format(estimatedPrice), fontSize = 18.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // AI CO-DESIGN ASSISTANT SECTION - Professional Polish Style
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = "AI", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(
                        text = "AI JEWELLERY CO-DESIGN",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "Describe your vision and let the artificial gemologist suggest exact design curves, detailing stories, and price evaluations.",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                OutlinedTextField(
                    value = userDraftPrompt,
                    onValueChange = { userDraftPrompt = it },
                    placeholder = { Text("A celestial crescent bracelet featuring moon markings with purple amethyst gem highlights...", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_draft_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary
                    ),
                    minLines = 2,
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        kbd?.hide()
                        viewModel.generateAiBracelet(userDraftPrompt)
                    })
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            kbd?.hide()
                            focus.clearFocus()
                            viewModel.generateAiBracelet(userDraftPrompt)
                        },
                        enabled = userDraftPrompt.isNotBlank() && aiState !is AiDesignState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("ai_cast_button")
                    ) {
                        if (aiState is AiDesignState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Design with AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // AI response layout box
                when (val result = aiState) {
                    is AiDesignState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.7f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = "Melting premium silver granules, hammering details...",
                                    style = LocalTextStyle.current.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 11.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                )
                            }
                        }
                    }

                    is AiDesignState.Error -> {
                        Text(
                            text = result.message,
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    is AiDesignState.Success -> {
                        val aiResponse = result.response
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFC4C6D0).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = aiResponse.title,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 16.sp
                                    )

                                    IconButton(
                                        onClick = { viewModel.resetAiDesign() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Reset AI", tint = MaterialTheme.colorScheme.secondary)
                                    }
                                }

                                SpecsChip("Craft style Suggestion", aiResponse.style, fillPrimary = true)

                                Text(
                                    text = aiResponse.description,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    SpecsChip("Finish", aiResponse.finish)
                                    SpecsChip("Gemstone", aiResponse.gemstone)
                                    if (aiResponse.engraving.isNotBlank()) {
                                        SpecsChip("Engrave", "'${aiResponse.engraving}'")
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Calculated weight", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                                        Text("${aiResponse.silverWeight}g Sterling", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            // Apply to active editor fields
                                            activeStyle = aiResponse.style
                                            activeFinish = aiResponse.finish
                                            activeGem = aiResponse.gemstone
                                            engravingText = aiResponse.engraving
                                            customDesignName = aiResponse.title
                                            
                                            // Auto-save to Custom Studio list
                                            viewModel.saveCustomBracelet(
                                                title = aiResponse.title,
                                                style = aiResponse.style,
                                                finish = aiResponse.finish,
                                                gemstone = aiResponse.gemstone,
                                                engraving = aiResponse.engraving,
                                                price = aiResponse.recommendedPrice,
                                                weight = aiResponse.silverWeight,
                                                aiPrompt = userDraftPrompt,
                                                aiDesc = aiResponse.description
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text("Apply & Save Design", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }

        // SAVED BESPOKE COLLECTION SECTION
        if (savedDesigns.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "MY BESPOKE CREATIONS",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )

                savedDesigns.forEach { design ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFC4C6D0).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                            .clickable { onCustomProductClick(design) }
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Tiny custom preview icon
                            val itemSpec = if (design.style.startsWith("Payal:")) {
                                val actStyle = design.style.substringAfter("Payal:")
                                "custom_payal:$actStyle:${design.finish}:${design.engraving}:${design.gemstone}"
                            } else {
                                "custom:${design.style}:${design.finish}:${design.gemstone}:${design.engraving}"
                            }
                            JewelleryVisualizer(
                                visualKey = itemSpec,
                                modifier = Modifier.size(54.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = design.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                val styleDisplay = if (design.style.startsWith("Payal:")) {
                                    "Custom Payal (" + design.style.substringAfter("Payal:") + ")"
                                } else {
                                    "${design.style} Bracelet"
                                }
                                Text(
                                    text = "$styleDisplay • ${design.finish} finish",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                if (design.engraving.isNotBlank()) {
                                    val label = if (design.style.startsWith("Payal:")) {
                                        "Bells: '${design.engraving}'"
                                    } else {
                                        "Engraved: '${design.engraving}'"
                                    }
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = CurrencyFormatter.format(design.price),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${design.silverWeight}g",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------- CUSTOM DESIGN DETAIL DIALOG -------------------
@Composable
fun CustomDesignDetailDialog(
    design: CustomDesignEntity,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onAddToCart: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14161B)),
            border = BorderStroke(1.dp, Color(0xFF23272F)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CUSTOM STUDIO CREATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA5C8E4),
                        letterSpacing = 1.5.sp
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                val specKey = if (design.style.startsWith("Payal:")) {
                    val actStyle = design.style.substringAfter("Payal:")
                    "custom_payal:$actStyle:${design.finish}:${design.engraving}:${design.gemstone}"
                } else {
                    "custom:${design.style}:${design.finish}:${design.gemstone}:${design.engraving}"
                }
                JewelleryVisualizer(
                    visualKey = specKey,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                Text(
                    text = design.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )

                val isPayal = design.style.startsWith("Payal:")
                val displayStyleStyle = if (isPayal) design.style.substringAfter("Payal:") else design.style
                val displayStyleLabel = if (isPayal) "Weave Type" else "Style"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SpecsChip(displayStyleLabel, displayStyleStyle)
                    SpecsChip("Finish", design.finish)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SpecsChip("Gemstone Base", design.gemstone, fillPrimary = design.gemstone != "None")
                    if (design.engraving.isNotBlank()) {
                        val label = if (isPayal) "Bells Style" else "Engrave Text"
                        SpecsChip(label, "'${design.engraving}'")
                    }
                }

                HorizontalDivider(color = Color(0xFF2A2E35))

                if (design.aiPrompt.isNotBlank()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Ideated Dream Prompt", fontSize = 11.sp, color = Color(0xFFA5C8E4), fontWeight = FontWeight.Bold)
                        Text(
                            text = "\"${design.aiPrompt}\"",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Design Narrative", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                    val defaultDesc = if (isPayal) {
                        "Hand-woven premium silver Payal anklet. Every link is custom structured from raw Sterling Silver wire, with dangling chime soundscapes and meticulously hand-constructed clasps."
                    } else {
                        "Hand-constructed bracelet. Each item is custom structured from solid Sterling Silver wires or sheets, using ancient silversmith practices and finished by hand in our studio."
                    }
                    Text(
                        text = design.aiDescription.ifBlank { defaultDesc },
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 18.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Est. Silver Weight", fontSize = 10.sp, color = Color(0xFF64748B))
                        Text("${design.silverWeight}g", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(
                            text = CurrencyFormatter.format(design.price),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA5C8E4)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onDelete,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A2A30)),
                            contentPadding = PaddingValues(10.dp),
                            modifier = Modifier.testTag("delete_design")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                        }

                        Button(
                            onClick = onAddToCart,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCBD5E1)),
                            modifier = Modifier.testTag("add_custom_to_cart")
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Add", tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Purchase", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ------------------- FAVORITES / WISHLIST SCREEN -------------------
@Composable
fun WishlistScreen(
    viewModel: JewelleryViewModel,
    onProductClick: (JewelleryProduct) -> Unit
) {
    val items by viewModel.wishlistItems.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "FAVORITE TREASURES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Saved For Later",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(14.dp))

        if (items.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Empty",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No saved favorites",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap the heart icons while browsing our premium rings, necklaces, and custom creations to save them here.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(items, key = { it.id }) { fav ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFC4C6D0).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                            .clickable {
                                if (fav.productId != -1) {
                                    val realProd = JewelleryCatalog.getProductById(fav.productId)
                                    if (realProd != null) onProductClick(realProd)
                                }
                            }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            JewelleryVisualizer(
                                visualKey = fav.imageSpec,
                                modifier = Modifier.size(60.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = fav.category.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = fav.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = CurrencyFormatter.format(fav.price),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = {
                                    viewModel.toggleWishlist(
                                        fav.productId,
                                        fav.title,
                                        fav.category,
                                        fav.price,
                                        fav.imageSpec
                                    )
                                }) {
                                    Icon(Icons.Default.Favorite, contentDescription = "Unfavorite", tint = Color(0xFFEF4444))
                                }

                                Button(
                                    onClick = {
                                        viewModel.addToCart(
                                            fav.productId,
                                            fav.title,
                                            fav.category,
                                            fav.price,
                                            fav.imageSpec,
                                            fav.metadata
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text("Add Bag", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------- SHOPPING BAG / CART SCREEN -------------------
@Composable
fun CartScreen(
    viewModel: JewelleryViewModel,
    discountApplied: Boolean,
    couponText: String,
    onCouponChange: (String) -> Unit,
    onApplyCoupon: () -> Unit,
    onCheckout: () -> Unit
) {
    val items by viewModel.cartItems.collectAsStateWithLifecycle()

    val subtotal = remember(items) { items.sumOf { it.price * it.quantity } }
    val discount = remember(subtotal, discountApplied) { if (discountApplied) subtotal * 0.15 else 0.0 }
    val total = remember(subtotal, discount) { subtotal - discount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "YOUR SHOPPING BAG",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Ready For Checkout",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(14.dp))

        if (items.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Empty Bag",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Your bag is empty",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Browse our high-quality collections of fine rings, necklaces, or design a custom-sculpted artisan bracelet in our studio.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Cart Items list
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFC4C6D0).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                JewelleryVisualizer(
                                    visualKey = item.imageSpec,
                                    modifier = Modifier.size(64.dp)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (item.metadata.isNotBlank()) {
                                        Text(
                                            text = item.metadata,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            lineHeight = 12.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = CurrencyFormatter.format(item.price),
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Decrement
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE1E2EC))
                                            .clickable { viewModel.decrementCartQuantity(item) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Less", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                    }

                                    Text(
                                        text = item.quantity.toString(),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )

                                    // Increment
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE1E2EC))
                                            .clickable { viewModel.incrementCartQuantity(item) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "More", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(14.dp))
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    // Remove Trash
                                    IconButton(
                                        onClick = { viewModel.removeFromCart(item) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444).copy(alpha = 0.8f))
                                    }
                                }
                            }
                        }
                    }
                }

                // Promo Coupon Code Input
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFC4C6D0).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = couponText,
                            onValueChange = onCouponChange,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("coupon_input"),
                            placeholder = { Text("Coupon Code (SILVER925)", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color(0xFFC4C6D0),
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            ),
                            singleLine = true
                        )

                        Button(
                            onClick = onApplyCoupon,
                            enabled = couponText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text("Apply", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // Totals Sheet
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), fontSize = 13.sp)
                            Text(CurrencyFormatter.format(subtotal), color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        if (discountApplied) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Coupon Discount (15%)", color = Color(0xFF10B981), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("-${CurrencyFormatter.format(discount)}", color = Color(0xFF10B981), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Shipment (Bespoke Boxed)", color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f), fontSize = 13.sp)
                            Text("FREE", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Outlay", color = MaterialTheme.colorScheme.primary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = CurrencyFormatter.format(total),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onCheckout,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .testTag("checkout_button")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Checkout", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Purchase Masterworks", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
