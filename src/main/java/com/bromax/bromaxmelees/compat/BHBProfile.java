package com.bromax.bromaxmelees.compat;

import java.util.List;

/** Safe BHB data container — no BHB classes referenced, usable anywhere in BMM. */
public record BHBProfile(String category, List<BHBProfile.Attack> attacks, int totalWeight) {

    public record Attack(int weight, float speedMul, float damageMul, int hitDelay) {}
}
