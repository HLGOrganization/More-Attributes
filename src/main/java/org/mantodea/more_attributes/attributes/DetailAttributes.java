package org.mantodea.more_attributes.attributes;

import net.minecraft.world.entity.ai.attributes.Attribute;

import static org.mantodea.more_attributes.utils.AttributeUtils.rangedDetail;

public class DetailAttributes {
    public static String EquipLoadMax_ID = "equip_load_max";
    public static Attribute EquipLoadMax = rangedDetail(EquipLoadMax_ID, 1200, 0, Double.MAX_VALUE).setSyncable(true);
    public static String EquipLoadCurrent_ID = "equip_load_current";
    public static Attribute EquipLoadCurrent = rangedDetail(EquipLoadCurrent_ID, 0, 0, Double.MAX_VALUE).setSyncable(true);
    public static String RangedDamage_ID = "ranged_damage";
    public static Attribute RangedDamage = rangedDetail(RangedDamage_ID, 1, 0, Double.MAX_VALUE).setSyncable(true);
    public static String MeleeDamage_ID = "melee_damage";
    public static Attribute MeleeDamage = rangedDetail(MeleeDamage_ID, 1, 0, Double.MAX_VALUE).setSyncable(true);
    public static String LifeRegeneration_ID = "life_regeneration";
    public static Attribute LifeRegeneration = rangedDetail(LifeRegeneration_ID, 1, -Double.MAX_VALUE, Double.MAX_VALUE).setSyncable(true);
    public static String HungerConsumption_ID = "hunger_consumption";
    public static Attribute HungerConsumption = rangedDetail(HungerConsumption_ID, 1, 0, Double.MAX_VALUE).setSyncable(true);
    public static String JumpForce_ID = "jump_force";
    public static Attribute JumpForce = rangedDetail(JumpForce_ID, 1, 0, Double.MAX_VALUE).setSyncable(true);
    public static String CriticalChance_ID = "critical_chance";
    public static Attribute CriticalChance = rangedDetail(CriticalChance_ID, 0.04, 0, 1).setSyncable(true);
    public static String CriticalDamage_ID = "critical_damage";
    public static Attribute CriticalDamage = rangedDetail(CriticalDamage_ID, 1, 0, Double.MAX_VALUE).setSyncable(true);
    public static String DamageReduction_ID = "damage_reduction";
    public static Attribute DamageReduction = rangedDetail(DamageReduction_ID, 0, 0, 1).setSyncable(true);

    public static void triggerClassLoader() {}
}
