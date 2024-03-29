package games.negative.framework.event.armor.internal;

public enum EquipMethod {

    /**
     * When you shift click an armor piece to equip or unequip
     */
    SHIFT_CLICK,
    /**
     * When you drag and drop the item to equip or unequip
     */
    DRAG,
    /**
     * When you right-click an armor piece in the hotbar without the inventory open to equip.
     */
    HOTBAR,
    /**
     * When you press the hotbar slot number while hovering over the armor slot to equip or unequip
     */
    HOTBAR_SWAP,
    /**
     * When in range of a dispenser that shoots an armor piece to equip.
     */
    DISPENSER,
    /**
     * When an armor piece breaks to unequip
     */
    BROKE,
    /**
     * When you die causing all armor to unequip
     */
    DEATH

}
