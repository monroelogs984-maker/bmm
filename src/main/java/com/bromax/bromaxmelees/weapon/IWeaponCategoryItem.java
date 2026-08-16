package com.bromax.bromaxmelees.weapon;

/**
 * Implemented by any item that belongs to a BMM weapon category.
 * The tooltip system reads traits from the category's WeaponProfile automatically,
 * so addon items only need to implement this interface — no other tooltip wiring needed.
 */
public interface IWeaponCategoryItem {
    WeaponCategory getWeaponCategory();
}
