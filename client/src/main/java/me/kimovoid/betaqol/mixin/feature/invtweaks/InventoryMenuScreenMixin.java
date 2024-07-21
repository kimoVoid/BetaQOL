package me.kimovoid.betaqol.mixin.feature.invtweaks;

import me.kimovoid.betaqol.BetaQOL;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.menu.InventoryMenuScreen;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * This is a port of InventoryTweaks for Babric.
 * All credits to telvarost and everyone involved in that project.
 * <a href="https://github.com/telvarost/InventoryTweaks-StationAPI">View here</a>
 */
@Mixin(InventoryMenuScreen.class)
public abstract class InventoryMenuScreenMixin extends Screen {
    @Shadow
    protected abstract InventorySlot getHoveredSlot(int x, int y);

    @Shadow
    public InventoryMenu menu;

    @Shadow
    protected abstract boolean isMouseOverSlot(InventorySlot slot, int x, int Y);

    @Unique private InventorySlot slot;

    @Unique InventorySlot lastRMBSlot = null;

    @Unique InventorySlot lastLMBSlot = null;

    @Unique int lastRMBSlotId = -1;

    @Unique int lastLMBSlotId = -1;

    @Unique
    private ItemStack leftClickMouseTweaksPersistentStack = null;

    @Unique
    private ItemStack leftClickPersistentStack = null;

    @Unique
    private ItemStack rightClickPersistentStack = null;

    @Unique
    private boolean isLeftClickDragMouseTweaksStarted = false;

    @Unique
    private boolean isLeftClickDragStarted = false;

    @Unique
    private boolean isRightClickDragStarted = false;

    @Unique
    private final List<InventorySlot> leftClickHoveredSlots = new ArrayList<>();

    @Unique final List<InventorySlot> rightClickHoveredSlots = new ArrayList<>();

    @Unique Integer leftClickItemAmount;

    @Unique Integer rightClickItemAmount;

    @Unique final List<Integer> leftClickExistingAmount = new ArrayList<>();

    @Unique final List<Integer> rightClickExistingAmount = new ArrayList<>();

    @Unique List<Integer> leftClickAmountToFillPersistent = new ArrayList<>();

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    protected void inventoryTweaks_mouseClicked(int mouseX, int mouseY, int button, CallbackInfo ci) {
        isLeftClickDragMouseTweaksStarted = false;

        /* Check if client is on a server */
        boolean isClientOnServer = minecraft.isMultiplayer();

        /* Right-click */
        if (button == 1) {
            boolean exitFunction = false;

            /* Should click cancel Left-click + Drag */
            if (!inventoryTweaks_cancelLeftClickDrag(isClientOnServer)) {

                /* Handle Right-click */
                if (BetaQOL.CONFIG.leftClickDrag.get()) {
                    exitFunction = inventoryTweaks_handleRightClick(mouseX, mouseY);
                }
            } else {
                exitFunction = true;
            }

            if (exitFunction) {
                /* Handle if a button was clicked */
                super.mouseClicked(mouseX, mouseY, button);
                ci.cancel();
                return;
            }
        }

        /* Left-click */
        if (button == 0) {
            boolean exitFunction = false;

            /* Should click cancel Right-click + Drag */
            if (!inventoryTweaks_cancelRightClickDrag(isClientOnServer)) {

                /* Handle Left-click */
                ItemStack cursorStack = minecraft.player.inventory.getCursorStack();
                InventorySlot clickedSlot = this.getHoveredSlot(mouseX, mouseY);
                if (cursorStack != null) {
                    if (BetaQOL.CONFIG.leftClickDrag.get()) {
                        exitFunction = inventoryTweaks_handleLeftClickWithItem(cursorStack, clickedSlot, isClientOnServer);
                    }
                } else {
                    exitFunction = inventoryTweaks_handleLeftClickWithoutItem(clickedSlot);
                }
            } else {
                exitFunction = true;
            }

            if (exitFunction) {
                /* Handle if a button was clicked */
                super.mouseClicked(mouseX, mouseY, button);
                ci.cancel();
                return;
            }
        }
    }

    @Inject(method = "mouseReleased", at = @At("RETURN"))
    private void inventoryTweaks_mouseReleasedOrSlotChanged(int mouseX, int mouseY, int button, CallbackInfo ci) {
        slot = this.getHoveredSlot(mouseX, mouseY);

        /* Do nothing if mouse is not over a slot */
        if (slot == null)
            return;

        if (BetaQOL.CONFIG.scrollWheelTweaks.get()) {
            if (!minecraft.isMultiplayer()) {
                int currentWheelDegrees = Mouse.getDWheel();
                if ((0 != currentWheelDegrees)
                        && (!isLeftClickDragStarted)
                        && (!isRightClickDragStarted)
                ) {
                    inventoryTweaks_handleScrollWheel(currentWheelDegrees);
                }
            }
        }

        /* Right-click + Drag logic = distribute one item from held items to each slot */
        if ( (button == -1)
                && ( Mouse.isButtonDown(1) )
                && (!isLeftClickDragStarted)
                && (!isLeftClickDragMouseTweaksStarted)
                && (rightClickPersistentStack != null)) {
            ItemStack slotItemToExamine = slot.getStack();

            /* Do nothing if slot item does not match held item or if the slot is full */
            if (  (null != slotItemToExamine)
                    && (  (!slotItemToExamine.matchesItem(rightClickPersistentStack))
                    || (slotItemToExamine.size == rightClickPersistentStack.getMaxSize())
            )
            ) {
                return;
            }

            /* Do nothing if there are no more items to distribute */
            ItemStack cursorStack = minecraft.player.inventory.getCursorStack();
            if (null == cursorStack) {
                return;
            }

            if (!rightClickHoveredSlots.contains(slot)) {
                inventoryTweaks_handleRightClickDrag(slotItemToExamine);
            } else if (BetaQOL.CONFIG.tweakRMB.get()) {
                inventoryTweaks_handleRightClickDragMouseTweaks();
            }
        } else {
            inventoryTweaks_resetRightClickDragVariables();
        }

        /* Left-click + Drag logic = evenly distribute held items over slots */
        if (  ( button == -1 )
                && ( Mouse.isButtonDown(0) )
                && (!isRightClickDragStarted)
        ) {
            if (isLeftClickDragMouseTweaksStarted) {
                inventoryTweaks_handleLeftClickDragMouseTweaks();
            } else if ( leftClickPersistentStack != null ) {
                if (inventoryTweaks_handleLeftClickDrag()) {
                    return;
                }
            } else {
                inventoryTweaks_resetLeftClickDragVariables();
            }
        } else {
            inventoryTweaks_resetLeftClickDragVariables();
        }
    }

    @Unique private void inventoryTweaks_handleScrollWheel(int wheelDegrees) {
        ItemStack cursorStack = minecraft.player.inventory.getCursorStack();
        ItemStack slotItemToExamine = slot.getStack();

        if (  (null != cursorStack)
                || (null != slotItemToExamine)
        )
        {
            //boolean isShiftKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
            boolean transferAllowed = true;
            float numberOfTurns = (float)wheelDegrees / 120.0f;
            int cursorStackAmount = 0;
            int slotStackAmount = 0;
            ItemStack itemBeingTransfered = null;

            if (null != cursorStack) {
                itemBeingTransfered = cursorStack;
                cursorStackAmount = cursorStack.size;
            }

            if (null != slotItemToExamine) {
                itemBeingTransfered = slotItemToExamine;
                slotStackAmount = slotItemToExamine.size;
            }

            if (  (null != cursorStack)
                    && (null != slotItemToExamine)
            ) {
                transferAllowed = cursorStack.matchesItem(slotItemToExamine);
            }

            if (transferAllowed) {
                inventoryTweaks_scrollCursorSlotTransfer(numberOfTurns, cursorStackAmount, slotStackAmount, itemBeingTransfered);
            }
        }
    }

    @Unique private void inventoryTweaks_scrollCursorSlotTransfer(float numTurns, int cursorAmount, int slotAmount, ItemStack transferItem) {
        if (BetaQOL.CONFIG.invertScrollCursorSlotDirection.get()) {
            numTurns *= -1;
        }

        if (0 > numTurns) {
            /* Transfer items to slot from cursor */
            if (0 != cursorAmount) {
                for (int turnIndex = 0; turnIndex < abs(numTurns); turnIndex++) {
                    if (slotAmount != transferItem.getMaxSize()) {
                        if (0 == (cursorAmount - 1)) {
                            minecraft.player.inventory.setCursorStack(null);
                        } else {
                            minecraft.player.inventory.setCursorStack(new ItemStack(transferItem.itemId, (cursorAmount - 1), transferItem.getDamage()));
                        }
                        slot.setStack(new ItemStack(transferItem.itemId, (slotAmount + 1), transferItem.getDamage()));
                    }
                }
            }
        } else {
            /* Transfer items to cursor from slot */
            if (0 != slotAmount) {
                for (int turnIndex = 0; turnIndex < abs(numTurns); turnIndex++) {
                    if (cursorAmount != transferItem.getMaxSize()) {
                        if (0 == (slotAmount - 1)) {
                            slot.setStack(null);
                        } else {
                            slot.setStack(new ItemStack(transferItem.itemId, (slotAmount - 1), transferItem.getDamage()));
                        }
                        minecraft.player.inventory.setCursorStack(new ItemStack(transferItem.itemId, (cursorAmount + 1), transferItem.getDamage()));
                    }
                }
            }
        }
    }

    @Unique private boolean inventoryTweaks_handleRightClick(int mouseX, int mouseY) {
        /* Get held item */
        ItemStack cursorStack = minecraft.player.inventory.getCursorStack();

        /* Handle Right-click if an item is held */
        if (null != cursorStack) {

            /* Ensure a slot was clicked */
            InventorySlot clickedSlot = this.getHoveredSlot(mouseX, mouseY);
            if (null != clickedSlot) {

                /* Record how many items are in the slot */
                if (null != clickedSlot.getStack()) {

                    /* Let vanilla minecraft handle right click with an item onto a different item */
                    if (!cursorStack.matchesItem(clickedSlot.getStack())) {
                        return false;
                    }

                    rightClickExistingAmount.add(clickedSlot.getStack().size);
                } else {
                    rightClickExistingAmount.add(0);
                }

                /* Begin Right-click + Drag */
                if (rightClickPersistentStack == null && !isRightClickDragStarted) {
                    rightClickPersistentStack = cursorStack;
                    rightClickItemAmount = rightClickPersistentStack.size;
                    isRightClickDragStarted = true;
                }

                /* Handle initial Right-click */
                lastRMBSlotId = clickedSlot.id;
                lastRMBSlot = clickedSlot;
                if (BetaQOL.CONFIG.preferShiftRMB.get()) {
                    boolean isShiftKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
                    this.minecraft.interactionManager.clickSlot(this.menu.networkId, clickedSlot.id, 1, isShiftKeyDown, this.minecraft.player);

                    if (isShiftKeyDown) {
                        inventoryTweaks_resetRightClickDragVariables();
                    }
                } else {
                    this.minecraft.interactionManager.clickSlot(this.menu.networkId, clickedSlot.id, 1, false, this.minecraft.player);
                }

                return true;
            }
        }

        return false;
    }

    @Unique private void inventoryTweaks_handleRightClickDragMouseTweaks() {
        if (slot.id != lastRMBSlotId) {
            ItemStack cursorStack = minecraft.player.inventory.getCursorStack();

            if (null != cursorStack ) {
                /* Distribute one item to the slot */
                lastRMBSlotId = slot.id;
                this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 1, false, this.minecraft.player);
            }
        }
    }

    @Unique private void inventoryTweaks_handleRightClickDrag(ItemStack slotItemToExamine) {
        /* First slot is handled instantly in mouseClicked function */
        if (slot.id != lastRMBSlotId) {
            if (rightClickHoveredSlots.isEmpty())
            {
                /* Add slot to item distribution */
                rightClickHoveredSlots.add(lastRMBSlot);
            }

            /* Add slot to item distribution */
            rightClickHoveredSlots.add(slot);

            /* Record how many items are in the slot */
            if (null != slotItemToExamine) {
                rightClickExistingAmount.add(slotItemToExamine.size);
            }
            else
            {
                rightClickExistingAmount.add(0);
            }

            /* Distribute one item to the slot */
            lastRMBSlotId = slot.id;
            this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 1, false, this.minecraft.player);
        }
    }

    @Unique private boolean inventoryTweaks_cancelRightClickDrag(boolean isClientOnServer)
    {
        /* Cancel Right-click + Drag */
        if (isRightClickDragStarted) {
            if (rightClickHoveredSlots.size() > 1) {
                /* Slots cannot return to normal on a server */
                if (!isClientOnServer) {
                    /* Return all slots to normal */
                    minecraft.player.inventory.setCursorStack(new ItemStack(rightClickPersistentStack.itemId, rightClickItemAmount, rightClickPersistentStack.getDamage()));
                    for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < rightClickHoveredSlots.size(); leftClickHoveredSlotsIndex++) {
                        if (0 != rightClickExistingAmount.get(leftClickHoveredSlotsIndex)) {
                            rightClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(new ItemStack(rightClickPersistentStack.itemId, rightClickExistingAmount.get(leftClickHoveredSlotsIndex), rightClickPersistentStack.getDamage()));
                        } else {
                            rightClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(null);
                        }
                    }
                }

                /* Reset Right-click + Drag variables and exit function */
                inventoryTweaks_resetRightClickDragVariables();

                return true;
            }
        }

        return false;
    }

    @Unique private void inventoryTweaks_resetRightClickDragVariables()
    {
        rightClickExistingAmount.clear();
        rightClickHoveredSlots.clear();
        rightClickPersistentStack = null;
        rightClickItemAmount = 0;
        isRightClickDragStarted = false;
    }

    @Unique private boolean inventoryTweaks_handleLeftClickWithItem(ItemStack cursorStack, InventorySlot clickedSlot, boolean isClientOnServer) {
        /* Ensure a slot was clicked */
        if (null != clickedSlot) {

            /* Record how many items are in the slot and how many items are needed to fill the slot */
            if (null != clickedSlot.getStack()) {

                if (null != cursorStack) {
                    /* Let vanilla minecraft handle left click with an item onto any item */
                    if (isClientOnServer) {
                        return false;
                    }

                    /* Let vanilla minecraft handle left click with an item onto a different item */
                    if (!cursorStack.matchesItem(clickedSlot.getStack()) ) {
                        return false;
                    }
                }

                leftClickAmountToFillPersistent.add(cursorStack.getMaxSize() - clickedSlot.getStack().size);
                leftClickExistingAmount.add(clickedSlot.getStack().size);
            } else {
                leftClickAmountToFillPersistent.add(cursorStack.getMaxSize());
                leftClickExistingAmount.add(0);
            }

            /* Begin Left-click + Drag */
            if (leftClickPersistentStack == null && !isLeftClickDragStarted) {
                leftClickPersistentStack = cursorStack;
                leftClickItemAmount = leftClickPersistentStack.size;
                isLeftClickDragStarted = true;
            }

            /* Handle initial Left-click */
            lastLMBSlotId = clickedSlot.id;
            lastLMBSlot = clickedSlot;
            if (BetaQOL.CONFIG.preferShiftLMB.get()) {
                boolean isShiftKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
                this.minecraft.interactionManager.clickSlot(this.menu.networkId, clickedSlot.id, 0, isShiftKeyDown, this.minecraft.player);

                if (isShiftKeyDown) {
                    inventoryTweaks_resetLeftClickDragVariables();
                    leftClickMouseTweaksPersistentStack = cursorStack;
                    isLeftClickDragMouseTweaksStarted = true;
                }
            } else {
                this.minecraft.interactionManager.clickSlot(this.menu.networkId, clickedSlot.id, 0, false, this.minecraft.player);
            }

            return true;
        }

        return false;
    }

    @Unique private boolean inventoryTweaks_handleLeftClickWithoutItem(InventorySlot clickedSlot) {
        isLeftClickDragMouseTweaksStarted = true;

        /* Ensure a slot was clicked */
        if (clickedSlot != null) {
            /* Get info for MouseTweaks `Left-Click + Drag` mechanics */
            leftClickMouseTweaksPersistentStack = clickedSlot.getStack();

            /* Handle initial Left-click */
            lastLMBSlotId = clickedSlot.id;
            lastLMBSlot = clickedSlot;
            boolean isShiftKeyDown = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
            this.minecraft.interactionManager.clickSlot(this.menu.networkId, clickedSlot.id, 0, isShiftKeyDown, this.minecraft.player);

            return true;
        } else {
            /* Get info for MouseTweaks `Left-Click + Drag` mechanics */
            leftClickMouseTweaksPersistentStack = null;
        }

        return false;
    }

    @Unique private void inventoryTweaks_handleLeftClickDragMouseTweaks() {
        if (slot.id != lastLMBSlotId) {
            lastLMBSlotId = slot.id;

            ItemStack slotItemToExamine = slot.getStack();
            if (null != slotItemToExamine)
            {
                if (BetaQOL.CONFIG.shiftClickAnyLMB.get() && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
                    this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 0, true, this.minecraft.player);
                }

                if (null != leftClickMouseTweaksPersistentStack)
                {
                    if (slotItemToExamine.matchesItem(leftClickMouseTweaksPersistentStack))
                    {
                        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                            if (BetaQOL.CONFIG.tweakLMBShiftClick.get())
                            {
                                this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 0, true, this.minecraft.player);
                            }
                        } else {
                            if (BetaQOL.CONFIG.tweakLMBPickUp.get()) {
                                ItemStack cursorStack = minecraft.player.inventory.getCursorStack();

                                if (cursorStack == null) {
                                    /* Pick up items from slot */
                                    this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 0, false, this.minecraft.player);
                                } else if (cursorStack.size < leftClickMouseTweaksPersistentStack.getMaxSize()) {
                                    int amountAbleToPickUp = leftClickMouseTweaksPersistentStack.getMaxSize() - cursorStack.size;
                                    int amountInSlot = slotItemToExamine.size;

                                    /* Pick up items from slot */
                                    if (amountInSlot <= amountAbleToPickUp) {
                                        this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 0, false, this.minecraft.player);
                                        this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 0, false, this.minecraft.player);
                                    } else if (cursorStack.size == leftClickMouseTweaksPersistentStack.getMaxSize()) {
                                        slot.setStack(new ItemStack(leftClickMouseTweaksPersistentStack.itemId, cursorStack.size, leftClickMouseTweaksPersistentStack.getDamage()));
                                        minecraft.player.inventory.setCursorStack(new ItemStack(leftClickMouseTweaksPersistentStack.itemId, amountInSlot, leftClickMouseTweaksPersistentStack.getDamage()));
                                    } else {
                                        this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 0, false, this.minecraft.player);

                                        slotItemToExamine = slot.getStack();
                                        cursorStack = minecraft.player.inventory.getCursorStack();
                                        amountInSlot = slotItemToExamine.size;

                                        slot.setStack(new ItemStack(leftClickMouseTweaksPersistentStack.itemId, cursorStack.size, leftClickMouseTweaksPersistentStack.getDamage()));
                                        minecraft.player.inventory.setCursorStack(new ItemStack(leftClickMouseTweaksPersistentStack.itemId, amountInSlot, leftClickMouseTweaksPersistentStack.getDamage()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Unique private boolean inventoryTweaks_handleLeftClickDrag()
    {
        /* Do nothing if slot has already been added to Left-click + Drag logic */
        if (!leftClickHoveredSlots.contains(slot)) {
            ItemStack slotItemToExamine = slot.getStack();

            /* Check if client is on a server */
            boolean isClientOnServer = minecraft.isMultiplayer();

            /* Do nothing if slot item does not match held item */
            if (null != slotItemToExamine){

                if (isClientOnServer) {
                    return true;
                }

                if (!slotItemToExamine.matchesItem(leftClickPersistentStack)) {
                    return true;
                }
            }

            /* Do nothing if there are no more items to distribute */
            if (1.0 == (double)leftClickItemAmount / (double)leftClickHoveredSlots.size()) {
                return true;
            }

            /* First slot is handled instantly in mouseClicked function */
            if (slot.id != lastLMBSlotId) {
                if (leftClickHoveredSlots.isEmpty())
                {
                    /* Add slot to item distribution */
                    leftClickHoveredSlots.add(lastLMBSlot);
                }

                /* Add slot to item distribution */
                leftClickHoveredSlots.add(slot);

                /* Record how many items are in the slot and how many items are needed to fill the slot */
                if (null != slotItemToExamine) {
                    leftClickAmountToFillPersistent.add(leftClickPersistentStack.getMaxSize() - slotItemToExamine.size);
                    leftClickExistingAmount.add(slotItemToExamine.size);
                }
                else
                {
                    leftClickAmountToFillPersistent.add(leftClickPersistentStack.getMaxSize());
                    leftClickExistingAmount.add(0);
                }

                /* Slots cannot return to normal on a server */
                List<Integer> leftClickAmountToFill = new ArrayList<>();
                if (!isClientOnServer) {
                    /* Return all slots to normal */
                    minecraft.player.inventory.setCursorStack(new ItemStack(leftClickPersistentStack.itemId, leftClickItemAmount, leftClickPersistentStack.getDamage()));
                    for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < leftClickHoveredSlots.size(); leftClickHoveredSlotsIndex++) {
                        leftClickAmountToFill.add(leftClickAmountToFillPersistent.get(leftClickHoveredSlotsIndex));
                        if (0 != leftClickExistingAmount.get(leftClickHoveredSlotsIndex)) {
                            leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(new ItemStack(leftClickPersistentStack.itemId, leftClickExistingAmount.get(leftClickHoveredSlotsIndex), leftClickPersistentStack.getDamage()));
                        } else {
                            leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(null);
                        }
                    }
                }

                /* Prepare to distribute over slots */
                int numberOfSlotsRemainingToFill = leftClickHoveredSlots.size();
                int itemsPerSlot = leftClickItemAmount / numberOfSlotsRemainingToFill;
                int leftClickRemainingItemAmount = leftClickItemAmount;
                boolean rerunLoop;

                /* Slots cannot return to normal on a server */
                if (!isClientOnServer) {
                    /* Distribute fewer items to slots whose max stack size will be filled */
                    do {
                        rerunLoop = false;
                        itemsPerSlot = leftClickRemainingItemAmount / numberOfSlotsRemainingToFill;

                        if (0 != itemsPerSlot) {
                            for (int slotsToCheckIndex = 0; slotsToCheckIndex < leftClickAmountToFill.size(); slotsToCheckIndex++) {
                                if (0 != leftClickAmountToFill.get(slotsToCheckIndex) && leftClickAmountToFill.get(slotsToCheckIndex) < itemsPerSlot) {
                                    /* Just fill the slot and return */
                                    for (int fillTheAmountIndex = 0; fillTheAmountIndex < leftClickAmountToFill.get(slotsToCheckIndex); fillTheAmountIndex++) {
                                        this.minecraft.interactionManager.clickSlot(this.menu.networkId, leftClickHoveredSlots.get(slotsToCheckIndex).id, 1, false, this.minecraft.player);
                                    }

                                    leftClickRemainingItemAmount = leftClickRemainingItemAmount - leftClickAmountToFill.get(slotsToCheckIndex);
                                    leftClickAmountToFill.set(slotsToCheckIndex, 0);
                                    numberOfSlotsRemainingToFill--;
                                    rerunLoop = true;
                                }
                            }
                        }
                    } while (rerunLoop && 0 < numberOfSlotsRemainingToFill);
                } else {
                    /* Return slots to normal on when client is on a server */
                    for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < (leftClickHoveredSlots.size() - 1); leftClickHoveredSlotsIndex++)
                    {
                        ItemStack cursorStack = minecraft.player.inventory.getCursorStack();
                        if (leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).hasStack() && leftClickHoveredSlots.size() > 1)
                        {
                            if (cursorStack != null)
                            {
                                this.minecraft.interactionManager.clickSlot(this.menu.networkId, leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).id, 0, false, this.minecraft.player);
                            }
                            this.minecraft.interactionManager.clickSlot(this.menu.networkId, leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).id, 0, false, this.minecraft.player);
                        }
                    }
                }

                /* Distribute remaining items evenly over remaining slots that were not already filled to max stack size */
                for (int distributeSlotsIndex = 0; distributeSlotsIndex < leftClickHoveredSlots.size(); distributeSlotsIndex++) {
                    if (isClientOnServer) {
                        if (0 != leftClickAmountToFillPersistent.get(distributeSlotsIndex)) {
                            for (int addSlotIndex = 0; addSlotIndex < itemsPerSlot; addSlotIndex++) {
                                this.minecraft.interactionManager.clickSlot(this.menu.networkId, leftClickHoveredSlots.get(distributeSlotsIndex).id, 1, false, this.minecraft.player);
                            }
                        }
                    } else {
                        if (0 != leftClickAmountToFill.get(distributeSlotsIndex)) {
                            for (int addSlotIndex = 0; addSlotIndex < itemsPerSlot; addSlotIndex++) {
                                this.minecraft.interactionManager.clickSlot(this.menu.networkId, leftClickHoveredSlots.get(distributeSlotsIndex).id, 1, false, this.minecraft.player);
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    @Unique private boolean inventoryTweaks_cancelLeftClickDrag(boolean isClientOnServer)
    {
        /* Cancel Left-click + Drag */
        if (isLeftClickDragStarted) {
            if (leftClickHoveredSlots.size() > 1) {
                /* Check if client is running on a server or not */
                if (!isClientOnServer) {
                    /* Return all slots to normal */
                    minecraft.player.inventory.setCursorStack(new ItemStack(leftClickPersistentStack.itemId, leftClickItemAmount, leftClickPersistentStack.getDamage()));
                    for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < leftClickHoveredSlots.size(); leftClickHoveredSlotsIndex++) {
                        if (0 != leftClickExistingAmount.get(leftClickHoveredSlotsIndex)) {
                            leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(new ItemStack(leftClickPersistentStack.itemId, leftClickExistingAmount.get(leftClickHoveredSlotsIndex), leftClickPersistentStack.getDamage()));
                        } else {
                            leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).setStack(null);
                        }
                    }
                } else {
                    /* Return slots to normal on when client is on a server */
                    for (int leftClickHoveredSlotsIndex = 0; leftClickHoveredSlotsIndex < (leftClickHoveredSlots.size() - 1); leftClickHoveredSlotsIndex++)
                    {
                        ItemStack cursorStack = minecraft.player.inventory.getCursorStack();
                        if (leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).hasStack() && leftClickHoveredSlots.size() > 1)
                        {
                            if (cursorStack != null)
                            {
                                this.minecraft.interactionManager.clickSlot(this.menu.networkId, leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).id, 0, false, this.minecraft.player);
                            }
                            this.minecraft.interactionManager.clickSlot(this.menu.networkId, leftClickHoveredSlots.get(leftClickHoveredSlotsIndex).id, 0, false, this.minecraft.player);
                        }
                    }
                    this.minecraft.interactionManager.clickSlot(this.menu.networkId, leftClickHoveredSlots.get((leftClickHoveredSlots.size() - 1)).id, 0, false, this.minecraft.player);
                    this.minecraft.interactionManager.clickSlot(this.menu.networkId, leftClickHoveredSlots.get((leftClickHoveredSlots.size() - 1)).id, 0, false, this.minecraft.player);
                }

                /* Reset Left-click + Drag variables and exit function */
                inventoryTweaks_resetLeftClickDragVariables();
                return true;
            }
        }

        return false;
    }

    @Unique private void inventoryTweaks_resetLeftClickDragVariables() {
        leftClickExistingAmount.clear();
        leftClickAmountToFillPersistent.clear();
        leftClickHoveredSlots.clear();
        leftClickPersistentStack = null;
        leftClickMouseTweaksPersistentStack = null;
        leftClickItemAmount = 0;
        isLeftClickDragStarted = false;
        isLeftClickDragMouseTweaksStarted = false;
    }

    @Unique
    private boolean drawingHoveredSlot;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/inventory/menu/InventoryMenuScreen;isMouseOverSlot(Lnet/minecraft/inventory/slot/InventorySlot;II)Z"))
    private boolean inventoryTweaks_isMouseOverSlot(InventoryMenuScreen guiContainer, InventorySlot slot, int x, int y) {
        if (BetaQOL.CONFIG.dragGraphics.get()) {
            return (  (drawingHoveredSlot = rightClickHoveredSlots.contains(slot))
                    || (drawingHoveredSlot = leftClickHoveredSlots.contains(slot))
                    || isMouseOverSlot(slot, x, y)
            );
        } else {
            return isMouseOverSlot(slot, x, y);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/inventory/menu/InventoryMenuScreen;fillGradient(IIIIII)V", ordinal = 0))
    private void inventoryTweaks_fillGradient(InventoryMenuScreen instance, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        if (BetaQOL.CONFIG.dragGraphics.get() && this.drawingHoveredSlot) {
            this.fillGradient(startX, startY, endX, endY, 0x20ffffff, 0x20ffffff);
        } else {
            this.fillGradient(startX, startY, endX, endY, colorStart, colorEnd);
        }
    }

    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void inventoryTweaks_keyPressed(char character, int keyCode, CallbackInfo ci) {
        if (this.slot == null) {
            return;
        }

        if (BetaQOL.CONFIG.dropKeyInv.get()) {
            if (keyCode == BetaQOL.mc.options.dropKey.keyCode) {
                if (this.minecraft.player.inventory.getCursorStack() != null) {
                    return;
                }

                this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 0, false, this.minecraft.player);
                if (BetaQOL.CONFIG.ctrlDropStack.get()) {
                    this.minecraft.interactionManager.clickSlot(this.menu.networkId, -999, Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) ? 0 : 1, false, this.minecraft.player);
                } else {
                    this.minecraft.interactionManager.clickSlot(this.menu.networkId, -999, 1, false, this.minecraft.player);
                }
                this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 0, false, this.minecraft.player);
            }
        }

        if (BetaQOL.CONFIG.hotkeySwap.get()) {
            for (int i = 1; i < 10; i++) {
                int key = BetaQOL.INSTANCE.keybinds.getKeyFromCode(i + 1);
                if (keyCode == key) {
                    if (this.minecraft.player.inventory.getCursorStack() == null) {
                        this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 0, false, this.minecraft.player);
                    }
                    this.minecraft.interactionManager.clickSlot(this.menu.networkId, (this.menu.slots.size() - 10) + i, 0, false, this.minecraft.player);
                    this.minecraft.interactionManager.clickSlot(this.menu.networkId, slot.id, 0, false, this.minecraft.player);
                }
            }
        }
    }
}