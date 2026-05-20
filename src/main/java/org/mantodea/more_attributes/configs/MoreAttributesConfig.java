package org.mantodea.more_attributes.configs;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class MoreAttributesConfig {

    public static class Common {
        public ForgeConfigSpec.BooleanValue enableEquipLoad;

        public ForgeConfigSpec.ConfigValue<List<String>> classOrder;

        public Common(ForgeConfigSpec.Builder builder) {

            builder.push("common");

            enableEquipLoad = builder.comment("是否计算负重 / enable equips' load attribute or not").define("enable_equip_load", true);

            classOrder = builder.comment(
                "职业显示顺序",
                "列表中的职业将优先按此顺序排列，未列出的职业排在最后"
            ).define("class_order", List.of());

            builder.pop();
        }

        public static ForgeConfigSpec CommonSpec;

        public static Common Instance;

        static {
            var common = new ForgeConfigSpec.Builder().configure(Common::new);

            CommonSpec = common.getRight();

            Instance = common.getLeft();
        }
    }
}
