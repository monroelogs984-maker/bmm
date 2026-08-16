package com.bromax.bromaxmelees.weapon;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Internal weapon family groupings.
 *
 * Primarily used to make compat addon development easier — addon devs can
 * query a group to get all weapons of that family rather than listing every
 * WeaponCategory individually. Also used internally for trait defaults and
 * BHB category mapping.
 *
 * members() is computed on demand rather than statically to avoid circular
 * class initialization with WeaponCategory (which references WeaponGroup in
 * its own enum constructor).
 */
public enum WeaponGroup {

    SWORD,
    KNIFE,
    POLEARM,
    BLUNT,
    AXE,
    OTHER;

    public List<WeaponCategory> members() {
        return Arrays.stream(WeaponCategory.values())
                .filter(c -> c.group() == this)
                .collect(Collectors.toList());
    }
}
