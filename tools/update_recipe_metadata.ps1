param(
    [string]$RecipeRoot = "src/main/resources/data/concoction/recipe"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Web.Extensions
$JsonSerializer = New-Object System.Web.Script.Serialization.JavaScriptSerializer

function Get-ResultId($json) {
    if ($json -is [System.Collections.IDictionary] -and $json.Contains("result")) {
        $result = $json["result"]
        if ($result -is [System.Collections.IDictionary]) {
            if ($result.Contains("id")) { return [string]$result["id"] }
            if ($result.Contains("item")) { return [string]$result["item"] }
        }
    }

    return ""
}

function Get-ResultPath([string]$resultId) {
    if ([string]::IsNullOrWhiteSpace($resultId) -or -not $resultId.Contains(":")) {
        return ""
    }

    return $resultId.Split(":")[1]
}

function Get-IngredientReferences($value) {
    $references = New-Object System.Collections.Generic.List[string]

    function Add-References($node) {
        if ($null -eq $node) {
            return
        }

        if ($node -is [System.Collections.IDictionary]) {
            foreach ($key in $node.Keys) {
                if ($key -in @("item", "tag")) {
                    $rawValue = [string]$node[$key]
                    if (-not [string]::IsNullOrWhiteSpace($rawValue)) {
                        $references.Add($rawValue.ToLowerInvariant())
                    }
                } else {
                    Add-References $node[$key]
                }
            }
            return
        }

        if ($node -is [System.Collections.IEnumerable] -and $node -isnot [string]) {
            foreach ($item in $node) {
                Add-References $item
            }
        }
    }

    Add-References $value
    return $references
}

function Test-ContainsDye($references) {
    foreach ($reference in $references) {
        if ($reference -match "(^|:|/).+_dye$" -or $reference -match "(^|:)c:dyes($|/)") {
            return $true
        }
    }

    return $false
}

function Test-WoodenRecipeFamily([string]$resultPath, $references) {
    if ($resultPath -match "(^|_)(planks|wood|bark_wood|hyphae|boat|chest_boat|door|trapdoor|fence|fence_gate|stairs|slab|button|pressure_plate|sign|hanging_sign|campfire|cabinet)$") {
        return $true
    }

    foreach ($reference in $references) {
        if ($reference -match "(^|:|/).*(planks|log|wood|stem|hyphae|bamboo)(_|$)") {
            return $true
        }
    }

    return $false
}

function Test-Equipment([string]$resultPath) {
    return $resultPath -match "(^|_)(axe|hoe|pickaxe|shovel|sword|helmet|chestplate|leggings|boots|shield|arrow)$"
}

function Test-Building([string]$resultPath) {
    return $resultPath -match "(^|_)(block|cabinet|oven|churn|slab|stairs|door|trapdoor|fence|gate|planks|wood|pressure_plate|button|sign|hanging_sign|carpet)$"
}

function Test-Food([string]$resultPath) {
    return $resultPath -match "(^|_)(tea|brew|oil|milk_bottle|bottle|sandwich|cake|slice|cookie|soup|stew|sushi|dango|casserole|confit|rice|noodles|chocolate|hashbrowns|puffballs|potatoes|slop|omurice|tahchin|goulash|dish|egg|popcorn|bread|butter|corn|seeds|apple|carrot|beetroot|onion|tomato|cabbage|mint|cherry|lingonberry|glowberry|pinecone|sunflower)$"
}

function Get-RecipeGroup($json, [string]$resultPath, [string]$existingGroup) {
    if ([string]::IsNullOrWhiteSpace($resultPath)) {
        return $existingGroup
    }

    $references = Get-IngredientReferences $json
    $containsDye = Test-ContainsDye $references
    $isWoodenFamily = Test-WoodenRecipeFamily $resultPath $references

    if ($references.Contains("concoction:soap") -and $resultPath -notlike "soap*") {
        return "soap_cleaning"
    }

    if ($resultPath -eq "white_woven_carpet" -and -not $containsDye) {
        return "woven_carpet"
    }

    if ($resultPath -like "*_woven_carpet") {
        return "woven_carpet_dyeing"
    }

    if ($resultPath -like "small_*_pillow_block") {
        if ($containsDye) {
            return "small_pillow_dyeing"
        }

        return "small_pillow_conversion"
    }

    if ($resultPath -eq "pillow_block" -or $resultPath -match "(^|_)(white|orange|magenta|light_blue|yellow|lime|pink|gray|light_gray|cyan|purple|blue|brown|green|red|black)_pillow_block$") {
        if ($containsDye) {
            return "pillow_dyeing"
        }

        return "pillow_block"
    }

    if ($resultPath -match "_hanging_sign$") {
        return "hanging_sign"
    }

    if ($resultPath -match "_sign$") {
        return "wooden_sign"
    }

    if ($resultPath -match "_chest_boat$") {
        return "chest_boat"
    }

    if ($resultPath -match "_boat$") {
        return "boat"
    }

    if ($resultPath -match "_planks$") {
        return "planks"
    }

    if ($resultPath -match "(^|_)wood$" -or $resultPath -match "_bark_wood$" -or $resultPath -match "_hyphae$") {
        return "bark"
    }

    if ($resultPath -match "_button$" -and $isWoodenFamily) {
        return "wooden_button"
    }

    if ($resultPath -match "_pressure_plate$" -and $isWoodenFamily) {
        return "wooden_pressure_plate"
    }

    if ($resultPath -match "_door$" -and $isWoodenFamily) {
        return "wooden_door"
    }

    if ($resultPath -match "_trapdoor$" -and $isWoodenFamily) {
        return "wooden_trapdoor"
    }

    if ($resultPath -match "_fence_gate$" -and $isWoodenFamily) {
        return "wooden_fence_gate"
    }

    if ($resultPath -match "_fence$" -and $isWoodenFamily) {
        return "wooden_fence"
    }

    if ($resultPath -match "_stairs$" -and $isWoodenFamily) {
        return "wooden_stairs"
    }

    if ($resultPath -match "_slab$" -and $isWoodenFamily) {
        return "wooden_slab"
    }

    if ($resultPath -match "_campfire$") {
        return "campfire"
    }

    if ($resultPath -match "_cabinet$") {
        return "cabinet"
    }

    if ($resultPath -match "_oven$") {
        return "oven"
    }

    if ($resultPath -match "_churn$") {
        return "churn"
    }

    return $resultPath
}

function Get-CraftingCategory([string]$resultPath, [string]$existingCategory) {
    if ($resultPath -match "_boat$" -or $resultPath -match "_chest_boat$") {
        return "transportation"
    }

    if ($resultPath -match "_door$" -or $resultPath -match "_trapdoor$" -or $resultPath -match "_fence_gate$" -or $resultPath -match "_button$" -or $resultPath -match "_pressure_plate$") {
        return "redstone"
    }

    if ($resultPath -match "_sign$" -or $resultPath -match "_hanging_sign$") {
        return "misc"
    }

    if ($resultPath -match "_fence$") {
        return "decorations"
    }

    if ($resultPath -like "*_seeds" -and $resultPath -ne "sunflower_seeds") {
        return "misc"
    }

    if ($existingCategory -and $existingCategory -notin @("misc", "")) {
        return $existingCategory
    }

    if (Test-Equipment $resultPath) {
        return "equipment"
    }

    if (Test-Building $resultPath) {
        return "building"
    }

    if (Test-Food $resultPath) {
        return "food"
    }

    return "misc"
}

function Get-CookingCategory([string]$resultPath, [string]$existingCategory) {
    if ($existingCategory -and $existingCategory -notin @("misc", "")) {
        return $existingCategory
    }

    if (Test-Building $resultPath) {
        return "blocks"
    }

    if (Test-Food $resultPath) {
        return "food"
    }

    return "misc"
}

function Get-OvenCategory($json, [string]$resultPath, [string]$existingCategory) {
    $bottleItem = ""
    $bowlItem = ""

    if ($json.Contains("bottle_ingredient")) {
        $bottleIngredient = $json["bottle_ingredient"]
        if ($bottleIngredient -is [System.Collections.IDictionary] -and $bottleIngredient.Contains("item")) {
            $bottleItem = [string]$bottleIngredient["item"]
        }
    }

    if ($json.Contains("bowl_ingredient")) {
        $bowlIngredient = $json["bowl_ingredient"]
        if ($bowlIngredient -is [System.Collections.IDictionary] -and $bowlIngredient.Contains("item")) {
            $bowlItem = [string]$bowlIngredient["item"]
        }
    }

    $looksLikeBeverage = $resultPath -match "(^|_)(tea|brew|oil|sauce|drink|juice|bottle|condiment|syrup)($|_)"

    if ($existingCategory -and $existingCategory -notin @("", "food", "blocks", "misc")) {
        if ($existingCategory -eq "beverages_and_condiments" -and -not $looksLikeBeverage) {
            return "snacks_and_pastry"
        }

        return $existingCategory
    }

    if ($bowlItem -eq "minecraft:bucket" -or $resultPath -like "*bucket*" -or $resultPath -like "*casserole*") {
        return "feasts"
    }

    if ($bowlItem -eq "minecraft:bowl") {
        return "dishes_and_soups"
    }

    if (-not [string]::IsNullOrWhiteSpace($bowlItem)) {
        return "snacks_and_pastry"
    }

    if ($looksLikeBeverage) {
        return "beverages_and_condiments"
    }

    return "snacks_and_pastry"
}

function ConvertTo-OrderedData($value) {
    if ($null -eq $value) {
        return $null
    }

    if ($value -is [System.Collections.IDictionary]) {
        $ordered = [ordered]@{}
        foreach ($key in $value.Keys) {
            $ordered[$key] = ConvertTo-OrderedData $value[$key]
        }
        return $ordered
    }

    if ($value -is [System.Collections.IEnumerable] -and $value -isnot [string]) {
        $items = New-Object System.Collections.ArrayList
        foreach ($item in $value) {
            [void]$items.Add((ConvertTo-OrderedData $item))
        }
        return $items
    }

    if ($value -is [pscustomobject]) {
        $ordered = [ordered]@{}
        foreach ($prop in $value.PSObject.Properties) {
            $ordered[$prop.Name] = ConvertTo-OrderedData $prop.Value
        }
        return $ordered
    }

    return $value
}

function Set-IfChanged([System.Collections.IDictionary]$json, [string]$key, [string]$value) {
    if ([string]::IsNullOrWhiteSpace($value)) {
        return $false
    }

    $current = if ($json.Contains($key)) { [string]$json[$key] } else { "" }
    if ($current -eq $value) {
        return $false
    }

    $json[$key] = $value
    return $true
}

function Ensure-ArrayField([System.Collections.IDictionary]$json, [string]$key) {
    if (-not $json.Contains($key)) {
        return $false
    }

    $value = $json[$key]
    if ($value -is [System.Collections.IEnumerable] -and $value -isnot [string] -and $value -isnot [System.Collections.IDictionary]) {
        return $false
    }

    $json[$key] = @($value)
    return $true
}

$fullRecipeRoot = Resolve-Path $RecipeRoot
$files = Get-ChildItem -Path $fullRecipeRoot -Recurse -File -Filter *.json
$changedFiles = New-Object System.Collections.Generic.List[string]

foreach ($file in $files) {
    $json = ConvertTo-OrderedData ($JsonSerializer.DeserializeObject((Get-Content $file.FullName -Raw)))
    $type = [string]$json["type"]
    $resultId = Get-ResultId $json
    $resultPath = Get-ResultPath $resultId
    $existingCategory = if ($json.Contains("category")) { [string]$json["category"] } else { "" }
    $existingGroup = if ($json.Contains("group")) { [string]$json["group"] } else { "" }
    $updated = $false

    switch ($type) {
        "minecraft:crafting_shaped" {
            $updated = (Ensure-ArrayField $json "pattern") -or $updated
            $updated = (Set-IfChanged $json "group" (Get-RecipeGroup $json $resultPath $existingGroup)) -or $updated
            $updated = (Set-IfChanged $json "category" (Get-CraftingCategory $resultPath $existingCategory)) -or $updated
        }
        "minecraft:crafting_shapeless" {
            $updated = (Ensure-ArrayField $json "ingredients") -or $updated
            $updated = (Set-IfChanged $json "group" (Get-RecipeGroup $json $resultPath $existingGroup)) -or $updated
            $updated = (Set-IfChanged $json "category" (Get-CraftingCategory $resultPath $existingCategory)) -or $updated
        }
        "minecraft:smelting" {
            $updated = (Set-IfChanged $json "group" (Get-RecipeGroup $json $resultPath $existingGroup)) -or $updated
            $updated = (Set-IfChanged $json "category" (Get-CookingCategory $resultPath $existingCategory)) -or $updated
        }
        "minecraft:smoking" {
            $updated = (Set-IfChanged $json "group" (Get-RecipeGroup $json $resultPath $existingGroup)) -or $updated
            $updated = (Set-IfChanged $json "category" (Get-CookingCategory $resultPath $existingCategory)) -or $updated
        }
        "minecraft:campfire_cooking" {
            $updated = (Set-IfChanged $json "group" (Get-RecipeGroup $json $resultPath $existingGroup)) -or $updated
            $updated = (Set-IfChanged $json "category" (Get-CookingCategory $resultPath $existingCategory)) -or $updated
        }
        "concoction:oven" {
            $updated = (Ensure-ArrayField $json "crafting_ingredients") -or $updated
            $updated = (Set-IfChanged $json "group" (Get-RecipeGroup $json $resultPath $existingGroup)) -or $updated
            $updated = (Set-IfChanged $json "category" (Get-OvenCategory $json $resultPath $existingCategory)) -or $updated
        }
        default { }
    }

    if (-not $updated) {
        continue
    }

    $content = $json | ConvertTo-Json -Depth 100
    [System.IO.File]::WriteAllText($file.FullName, $content + [Environment]::NewLine, [System.Text.UTF8Encoding]::new($false))
    $changedFiles.Add($file.FullName.Substring((Resolve-Path ".").Path.Length + 1))
}

if ($changedFiles.Count -eq 0) {
    Write-Output "No recipe metadata updates were necessary."
    exit 0
}

Write-Output ("Updated {0} recipe files:" -f $changedFiles.Count)
$changedFiles | Sort-Object | ForEach-Object { $_ }
