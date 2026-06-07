package com.example.data.model

data class JewelleryProduct(
    val id: Int,
    val title: String,
    val category: String, // "Ring", "Necklace", "Bracelet"
    val price: Double,
    val description: String,
    val silverWeight: Double, // in grams
    val purity: String = "925 Sterling Silver",
    val gemstone: String = "None",
    val artisanNotes: String = "",
    val visualKey: String = "" // custom representation pattern
)

object JewelleryCatalog {
    val items = listOf(
        JewelleryProduct(
            id = 1,
            title = "Aether Celtic Weave",
            category = "Ring",
            price = 85.00,
            description = "A meticulously sculpted band boasting rich, ancient Celtic knotwork symbolizing eternity and continuity. Perfectly suited for daily wear or ritual occasions.",
            silverWeight = 4.8,
            purity = "925 Sterling Silver",
            gemstone = "None",
            artisanNotes = "Individually hand-polished with an antique oxidized finish in the grooves to draw deep contrast.",
            visualKey = "celtic_ring"
        ),
        JewelleryProduct(
            id = 2,
            title = "Obsidian Eclipse Signet",
            category = "Ring",
            price = 125.00,
            description = "A bold, masculine signet ring with an imposing, mirror-finished flat top featuring a genuine hand-cut square Black Onyx gem.",
            silverWeight = 8.2,
            purity = "925 Sterling Silver",
            gemstone = "Natural Black Onyx",
            artisanNotes = "Sourced from the heart of Oregon, each Onyx stone boasts its own deep onyx opacity.",
            visualKey = "eclipse_signet"
        ),
        JewelleryProduct(
            id = 3,
            title = "Aurora Moonstone Solitaire",
            category = "Ring",
            price = 115.00,
            description = "An ethereal statement ring featuring an oval-cut natural Rainbow Moonstone that shifts with deep internal iridescent blue fire.",
            silverWeight = 3.5,
            purity = "925 Sterling Silver",
            gemstone = "Rainbow Moonstone",
            artisanNotes = "Prong-set in a low-profile basket with high-polish ring shoulders.",
            visualKey = "moonstone_solitaire"
        ),
        JewelleryProduct(
            id = 4,
            title = "Luna Crescent Medallion",
            category = "Necklace",
            price = 145.00,
            description = "A delicate celestial crescent moon pendant cradling a micro-pave white sapphire constellation. Drapes from a shimmering 18-inch cable chain.",
            silverWeight = 5.6,
            purity = "925 Sterling Silver",
            gemstone = "White Sapphire",
            artisanNotes = "Finished with a bespoke lobster clasp and an adjustable 2-inch silver extender.",
            visualKey = "luna_necklace"
        ),
        JewelleryProduct(
            id = 5,
            title = "Valkyrie Wing Choker",
            category = "Necklace",
            price = 210.00,
            description = "A striking statement piece constructed from interlocking feather-textured silver links inspired by legendary Norse armor.",
            silverWeight = 12.8,
            purity = "925 Sterling Silver",
            gemstone = "None",
            artisanNotes = "Slightly brushed and oxidized by our head smith to create an heirloom, battle-tested steel-silver texture.",
            visualKey = "wing_choker"
        ),
        JewelleryProduct(
            id = 6,
            title = "Yggdrasil Locket Pendant",
            category = "Necklace",
            price = 175.00,
            description = "A functional silver locket carved with the intricate Tree of Life. Opens to reveal a dual photograph insert lined with soft velvet.",
            silverWeight = 7.4,
            purity = "925 Sterling Silver",
            gemstone = "None",
            artisanNotes = "Hinged gate closure, includes a heavy diamond-cut snake chain.",
            visualKey = "tree_locket"
        ),
        JewelleryProduct(
            id = 7,
            title = "Elysian Hammered Cuff",
            category = "Bracelet",
            price = 240.00,
            description = "A raw, organic solid cuff crafted of dense sterling silver. Each cuff features unique hammer blows, creating a faceted reflective surface.",
            silverWeight = 19.5,
            purity = "925 Sterling Silver",
            gemstone = "Raw Turquoise",
            artisanNotes = "Hand-hammered flat stock with rounded edges and open backs for customizable width sizing.",
            visualKey = "hammer_cuff"
        ),
        JewelleryProduct(
            id = 8,
            title = "Midgard Braided Torque",
            category = "Bracelet",
            price = 295.00,
            description = "An intricate ancient-weave wire bracelet twisting six heavy silver strands together, terminating in stylized high-relief dragon head collars.",
            silverWeight = 24.2,
            purity = "925 Sterling Silver",
            gemstone = "None",
            artisanNotes = "Oxidized and hand-buffed. Solid silver wire core ensures indestructible durability.",
            visualKey = "viking_torque"
        ),
        JewelleryProduct(
            id = 9,
            title = "Jodhpur Royal Ghungroo Payal",
            category = "Payal",
            price = 185.00,
            description = "An ornate 92.5% sterling silver bridal Payal adorned with a cascade of hand-tuned rhythmic ghungroo bells that create a soft, musical chime with every step.",
            silverWeight = 28.5,
            purity = "925 Pure Silver",
            gemstone = "None",
            artisanNotes = "Hand-assembled by master silver-smiths in Rajasthan. Extremely comfortable and secure hook closure.",
            visualKey = "jodhpur_payal"
        ),
        JewelleryProduct(
            id = 10,
            title = "Deccan Filigree Charm Payal",
            category = "Payal",
            price = 110.00,
            description = "A lightweight modern Payal intricately woven with fine silver threads, dressed with hanging star and leaf accents and a luminous center Turquoise gem.",
            silverWeight = 11.2,
            purity = "92.5% Sterling Silver",
            gemstone = "Natural Turquoise",
            artisanNotes = "Perfect for casual contemporary wear with a light and breezy dangle.",
            visualKey = "deccan_payal"
        ),
        JewelleryProduct(
            id = 11,
            title = "Ganga Antique Devotional Payal",
            category = "Payal",
            price = 225.00,
            description = "A heavy, classic double-chain heirloom Payal boasting a deep oxidized heritage finish, set with rich red Garnet cabochons and substantial chime bells.",
            silverWeight = 34.0,
            purity = "925 Pure Silver",
            gemstone = "Natural Garnet",
            artisanNotes = "Finished with beautiful hand-stamped traditional end caps and oxidized details to enhance the ancient aesthetic.",
            visualKey = "ganga_payal"
        )
    )

    fun getProductById(id: Int): JewelleryProduct? {
        return items.find { it.id == id }
    }
}
