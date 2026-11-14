package com.aurafit.app.data.nutrition

import com.aurafit.app.data.network.ParsedFoodItem
import com.aurafit.app.data.network.ParsedNutritionResponse
import com.aurafit.app.data.network.ParsedTotals

data class NutritionProfile(
    val displayName: String,
    val portion: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val description: String
)

object FoodNutritionDatabase {

    // Curated high-precision dictionary for Indian & Global staples
    private val FOOD_MAP = mapOf(
        // Indian Staples & Dishes
        "roti" to NutritionProfile("Whole Wheat Roti / Chapati", "1 medium roti (~40g)", 104, 3, 20, 1, "Complex carbs and fiber from whole wheat; low glycemic index staple."),
        "chapati" to NutritionProfile("Whole Wheat Chapati", "1 medium chapati (~40g)", 104, 3, 20, 1, "Fiber-rich whole grain flatbread cooked on dry griddle."),
        "naan" to NutritionProfile("Butter Naan", "1 medium naan (~90g)", 260, 8, 45, 6, "Tandoor baked leavened flatbread brushed with butter."),
        "paratha" to NutritionProfile("Aloo Paratha", "1 paratha (~150g)", 290, 6, 38, 13, "Whole wheat flatbread stuffed with spiced potato mixture."),
        "white rice" to NutritionProfile("Steamed Basmati Rice", "1 medium cup cooked (~160g)", 210, 4, 45, 1, "Easily digestible energy-dense carbohydrate source."),
        "cooked rice" to NutritionProfile("Cooked White Rice", "1 medium cup cooked (~160g)", 210, 4, 45, 1, "Easily digestible staple grain."),
        "fried rice" to NutritionProfile("Veg Fried Rice", "1 bowl (~200g)", 330, 6, 52, 11, "Wok-tossed rice with vegetables, light soy sauce, and aromatic seasoning."),
        "chicken biryani" to NutritionProfile("Chicken Biryani", "1 plate (~350g)", 540, 28, 62, 19, "Aromatic spiced basmati rice layered with marinated tender chicken pieces."),
        "biryani" to NutritionProfile("Vegetable Dum Biryani", "1 plate (~300g)", 420, 10, 68, 12, "Fragrant spiced basmati rice layered with garden vegetables and saffron."),
        "dal" to NutritionProfile("Yellow Dal Tadka", "1 bowl (~180g)", 175, 9, 22, 6, "Yellow lentils simmered with cumin, garlic, tomato, and ghee seasoning."),
        "dal makhani" to NutritionProfile("Dal Makhani", "1 bowl (~200g)", 280, 10, 24, 16, "Slow-cooked black lentils and kidney beans in rich creamy gravy."),
        "sambar" to NutritionProfile("Vegetable Sambar", "1 bowl (~200g)", 130, 5, 20, 4, "South Indian lentil stew with drumsticks, tamarind, and vegetable medley."),
        "idli" to NutritionProfile("Steamed Idli", "2 pieces (~120g)", 140, 4, 28, 1, "Steamed fermented rice and urad dal cakes; very gut-friendly and low fat."),
        "dosa" to NutritionProfile("Plain Crisp Dosa", "1 medium dosa (~120g)", 185, 4, 29, 6, "Crispy fermented crepe made from rice and black gram batter."),
        "masala dosa" to NutritionProfile("Crispy Masala Dosa", "1 medium dosa with aloo (~180g)", 310, 6, 44, 12, "Golden fermented crepe filled with savory turmeric spiced potatoes."),
        "poha" to NutritionProfile("Kanda Poha", "1 bowl (~180g)", 240, 5, 42, 6, "Flattened rice tempered with mustard seeds, curry leaves, onions, and peanuts."),
        "upma" to NutritionProfile("Rava Upma", "1 bowl (~180g)", 210, 5, 36, 6, "Savory semolina porridge cooked with vegetables and mild spices."),
        "samosa" to NutritionProfile("Spiced Potato Samosa", "1 large piece (~90g)", 260, 4, 30, 14, "Crisp golden pastry filled with spiced potato and green pea filling."),
        "paneer" to NutritionProfile("Paneer Butter Masala", "1 bowl (~200g)", 360, 14, 16, 28, "Cottage cheese cubes simmered in a luscious tomato, butter, and cashew gravy."),
        "shahi paneer" to NutritionProfile("Shahi Paneer", "1 bowl (~200g)", 340, 13, 15, 26, "Royal cottage cheese curry cooked with aromatic whole spices, yogurt, and nuts."),
        "palak paneer" to NutritionProfile("Palak Paneer", "1 bowl (~200g)", 270, 15, 11, 19, "Fresh spinach puree cooked with cottage cheese cubes, rich in iron and calcium."),
        "chole" to NutritionProfile("Chole / Chickpea Curry", "1 bowl (~200g)", 265, 12, 38, 8, "High-protein garbanzo beans slow-cooked with onion-tomato gravy and spices."),
        "rajma" to NutritionProfile("Rajma / Red Kidney Beans", "1 bowl (~200g)", 240, 13, 36, 5, "Slow-simmered kidney beans rich in dietary fiber, potassium, and plant protein."),
        "chicken curry" to NutritionProfile("Homestyle Chicken Curry", "1 bowl (~200g)", 310, 26, 8, 19, "Tender chicken cooked in traditional onion, ginger, garlic, and tomato curry."),
        "mutton curry" to NutritionProfile("Mutton Curry", "1 bowl (~200g)", 380, 24, 7, 28, "Slow-cooked goat meat in aromatic rich spiced gravy."),
        "curd" to NutritionProfile("Plain Fresh Curd / Dahi", "1 bowl (~150g)", 95, 5, 7, 5, "Probiotic fermented dairy supporting healthy gut microbiome."),
        "pav bhaji" to NutritionProfile("Pav Bhaji", "1 plate with 2 pav (~250g)", 460, 10, 65, 18, "Mashed spiced vegetable curry served with toasted buttered dinner rolls."),

        // Global Staples & Fast Foods
        "pizza" to NutritionProfile("Cheese & Tomato Pizza", "1 slice (~115g)", 285, 12, 32, 12, "Baked flatbread crust topped with tomato sauce, mozzarella, and herbs."),
        "cheeseburger" to NutritionProfile("Classic Cheeseburger", "1 burger (~165g)", 410, 22, 38, 20, "Grilled patty in toasted bun with cheddar cheese, lettuce, and condiments."),
        "veggie burger" to NutritionProfile("Veggie Burger", "1 burger (~160g)", 340, 14, 46, 12, "Plant-based vegetable and legume patty in whole grain bun."),
        "burger" to NutritionProfile("Classic Burger", "1 burger (~150g)", 375, 18, 36, 17, "Toasted bun with grilled protein, crisp greens, and savory sauce."),
        "french fries" to NutritionProfile("Crispy French Fries", "1 medium portion (~117g)", 365, 4, 48, 17, "Golden salted potato fries."),
        "sandwich" to NutritionProfile("Club Sandwich", "1 sandwich (~180g)", 320, 16, 34, 13, "Layered sliced bread with crisp vegetables, protein filling, and dressing."),
        "hot dog" to NutritionProfile("Hot Dog in Bun", "1 piece (~120g)", 290, 11, 24, 16, "Frankfurter served in a soft sliced bun with mustard."),
        "burrito" to NutritionProfile("Bean & Rice Burrito", "1 burrito (~220g)", 440, 16, 62, 15, "Warm flour tortilla wrapped around seasoned beans, rice, and salsa."),
        "taco" to NutritionProfile("Crispy Taco", "2 tacos (~170g)", 310, 14, 28, 16, "Crisp corn shells filled with seasoned filling, shredded lettuce, and cheese."),
        "pasta" to NutritionProfile("Pasta in Marinara Sauce", "1 bowl (~220g)", 290, 9, 52, 5, "Al dente pasta tossed in rich basil tomato marinara sauce."),
        "spaghetti" to NutritionProfile("Spaghetti Marinara", "1 bowl (~220g)", 290, 9, 52, 5, "Classic long pasta strands tossed in slow-simmered tomato sauce."),
        "noodles" to NutritionProfile("Stir-Fried Noodles", "1 bowl (~200g)", 320, 7, 50, 10, "Wheat noodles wok-tossed with scallions, bell peppers, and soy."),

        // Fresh Fruits & Produce
        "apple" to NutritionProfile("Fresh Crisp Apple", "1 medium apple (~182g)", 95, 0, 25, 0, "High in dietary fiber, polyphenols, vitamin C, and pectin."),
        "banana" to NutritionProfile("Fresh Ripe Banana", "1 medium banana (~118g)", 105, 1, 27, 0, "Rich in potassium, vitamin B6, natural sugars, and steady energy."),
        "orange" to NutritionProfile("Sweet Orange", "1 medium orange (~130g)", 62, 1, 15, 0, "Packed with immune-supporting vitamin C, folate, and antioxidants."),
        "strawberry" to NutritionProfile("Fresh Strawberries", "1 cup whole (~150g)", 49, 1, 12, 0, "Low-glycemic berry rich in anthocyanins, manganese, and vitamin C."),
        "mango" to NutritionProfile("Fresh Ripe Mango", "1 cup sliced (~165g)", 99, 1, 25, 1, "Tropical fruit rich in vitamin A, vitamin C, and digestive enzymes."),
        "watermelon" to NutritionProfile("Fresh Watermelon", "1 thick wedge (~280g)", 85, 2, 21, 0, "Over 90% water for hydration with high levels of lycopene."),
        "grapes" to NutritionProfile("Fresh Grapes", "1 cup (~150g)", 104, 1, 27, 0, "Sweet natural fruit rich in resveratrol and protective antioxidants."),
        "avocado" to NutritionProfile("Fresh Avocado", "Half avocado (~100g)", 160, 2, 9, 15, "Heart-healthy monounsaturated fatty acids, potassium, and fiber."),

        // Salads, Eggs & Lean Proteins
        "salad" to NutritionProfile("Garden Green Salad", "1 bowl (~200g)", 125, 4, 14, 6, "Mixed leafy greens, cucumber, tomato, and light vinaigrette dressing."),
        "boiled egg" to NutritionProfile("Hard Boiled Egg", "1 large egg (~50g)", 78, 6, 1, 5, "Complete bioavailable protein with all 9 essential amino acids and choline."),
        "omelette" to NutritionProfile("Two-Egg Omelette", "1 omelette (~120g)", 188, 13, 2, 14, "Fluffy pan-cooked eggs with herbs, high in protein and healthy fats."),
        "grilled chicken" to NutritionProfile("Grilled Chicken Breast", "1 breast portion (~165g)", 265, 52, 0, 6, "Lean high-protein staple; zero carbs and very low in saturated fat."),
        "steak" to NutritionProfile("Grilled Beef Steak", "1 steak (~200g)", 420, 48, 0, 24, "Iron-rich protein source supporting muscle growth and energy metabolism."),
        "salmon" to NutritionProfile("Pan-Seared Salmon", "1 fillet (~180g)", 370, 36, 0, 22, "Premium source of heart-supporting omega-3 fatty acids EPA and DHA."),
        "oatmeal" to NutritionProfile("Rolled Oatmeal", "1 bowl cooked (~230g)", 160, 6, 28, 3, "Beta-glucan soluble fiber champion supporting heart and cholesterol health."),
        "soup" to NutritionProfile("Vegetable Soup", "1 bowl (~220g)", 90, 3, 16, 2, "Warm comforting broth filled with garden vegetables and herbs.")
    )

    fun lookup(rawLabel: String): ParsedNutritionResponse {
        val clean = rawLabel.trim().lowercase()

        // 1. Direct key match
        FOOD_MAP[clean]?.let { return toResponse(it) }

        // 2. Substring search in our curated map
        for ((key, profile) in FOOD_MAP) {
            if (clean.contains(key) || key.contains(clean)) {
                return toResponse(profile)
            }
        }

        // 3. Category heuristics for unrecognized labels from the 2024 classes
        val profile = deriveFromLabel(rawLabel)
        return toResponse(profile)
    }

    private fun deriveFromLabel(label: String): NutritionProfile {
        val clean = label.lowercase()
        return when {
            clean.contains("cake") || clean.contains("pie") || clean.contains("pastry") || clean.contains("cookie") -> {
                NutritionProfile(label.replace("_", " ").trim(), "1 slice / piece (~90g)", 290, 4, 42, 12, "Baked sweet treat containing flour, sugar, and shortening.")
            }
            clean.contains("bread") || clean.contains("toast") || clean.contains("bagel") || clean.contains("roll") -> {
                NutritionProfile(label.replace("_", " ").trim(), "1 serving (~75g)", 190, 6, 36, 2, "Baked grain staple providing carbohydrates and B-vitamins.")
            }
            clean.contains("soup") || clean.contains("broth") || clean.contains("chowder") -> {
                NutritionProfile(label.replace("_", " ").trim(), "1 bowl (~220g)", 140, 5, 18, 5, "Nutritious savory soup prepared with simmering ingredients.")
            }
            clean.contains("salad") -> {
                NutritionProfile(label.replace("_", " ").trim(), "1 bowl (~180g)", 135, 4, 12, 7, "Fresh vegetable and herb blend rich in phytonutrients and hydration.")
            }
            clean.contains("chicken") || clean.contains("turkey") || clean.contains("duck") || clean.contains("poultry") -> {
                NutritionProfile(label.replace("_", " ").trim(), "1 serving (~180g)", 290, 32, 4, 16, "High-protein poultry prepared with regional seasonings.")
            }
            clean.contains("fish") || clean.contains("shrimp") || clean.contains("crab") || clean.contains("seafood") -> {
                NutritionProfile(label.replace("_", " ").trim(), "1 serving (~160g)", 220, 28, 2, 10, "Lean aquatic protein rich in essential minerals and omega fats.")
            }
            clean.contains("beef") || clean.contains("pork") || clean.contains("lamb") || clean.contains("meat") -> {
                NutritionProfile(label.replace("_", " ").trim(), "1 serving (~180g)", 340, 29, 3, 23, "Satisfying savory meat rich in iron, zinc, and protein.")
            }
            clean.contains("rice") -> {
                NutritionProfile(label.replace("_", " ").trim(), "1 bowl (~180g)", 230, 5, 48, 2, "Wholesome cooked rice dish offering easily digestible energy.")
            }
            clean.contains("curry") || clean.contains("stew") -> {
                NutritionProfile(label.replace("_", " ").trim(), "1 bowl (~200g)", 280, 14, 16, 17, "Rich aromatic spiced curry cooked with regional vegetables and sauce.")
            }
            clean.contains("noodle") || clean.contains("pasta") || clean.contains("spaghetti") || clean.contains("ramen") -> {
                NutritionProfile(label.replace("_", " ").trim(), "1 bowl (~200g)", 310, 8, 48, 9, "Warm savory noodles prepared with sauce and seasonings.")
            }
            else -> {
                val formattedName = label.split(" ")
                    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                NutritionProfile(formattedName, "1 standard portion (~150g)", 220, 8, 28, 8, "Nutritious meal recognized on-device by NutriSnap AI.")
            }
        }
    }

    private fun toResponse(p: NutritionProfile): ParsedNutritionResponse {
        val item = ParsedFoodItem(
            foodName = p.displayName,
            portion = p.portion,
            calories = p.calories,
            protein = p.protein,
            carbs = p.carbs,
            fat = p.fat,
            description = p.description
        )
        return ParsedNutritionResponse(
            items = listOf(item),
            totals = ParsedTotals(p.calories, p.protein, p.carbs, p.fat),
            summary = p.description
        )
    }
}
