//package cn.fd.ratziel.module.ui.inventory.internal.nms
//
//import net.minecraft.network.protocol.game.PacketPlayInWindowClick
//import net.minecraft.world.inventory.Container
//import net.minecraft.world.inventory.InventoryClickType
//import org.bukkit.event.inventory.ClickType
//import org.bukkit.event.inventory.InventoryAction
//import taboolib.module.nms.Packet
//import taboolib.module.nms.PacketReceiveEvent
//
//
///**
// * InventoryHandlerImpl
// *
// * @author TheFloodDragon
// * @since 2025/10/4 20:45
// */
//class InventoryHandlerImpl {
//
//    fun handleContainerClick(event: PacketReceiveEvent) {
//        val packetSrc = event.packet.source as PacketPlayInWindowClick
//        when (packetSrc.clickType) {
//            InventoryClickType.PICKUP -> {
//                if (packetSrc.buttonNum == 0) {
//                    ClickType.LEFT
//                } else if (packetSrc.buttonNum== 1) {
//                    ClickType.RIGHT
//                }
//
//                Container
//
//                if (packetSrc.buttonNum == 0 || packetSrc.buttonNum == 1) {
//                    action = InventoryAction.NOTHING
//                    if (slotNum == -999) {
//                        if (!this.player.containerMenu.getCarried().isEmpty()) {
//                            action = if (packetSrc.buttonNum == 0) InventoryAction.DROP_ALL_CURSOR else InventoryAction.DROP_ONE_CURSOR
//                        }
//                    } else if (slotNum < 0) {
//                        action = InventoryAction.NOTHING
//                    } else {
//                        val slot: Slot? = this.player.containerMenu.getSlot(slotNum)
//                        if (slot != null) {
//                            val clickedItem: ItemStack = slot.getItem()
//                            val cursor: ItemStack = this.player.containerMenu.getCarried()
//                            if (clickedItem.isEmpty()) {
//                                if (!cursor.isEmpty()) {
//                                    if (cursor.getItem() is BundleItem && cursor.has(DataComponents.BUNDLE_CONTENTS) && packetSrc.buttonNum !== 0) {
//                                        action = if (cursor.get(DataComponents.BUNDLE_CONTENTS).isEmpty())
//                                            InventoryAction.NOTHING
//                                        else
//                                            InventoryAction.PLACE_FROM_BUNDLE
//                                    } else {
//                                        action = if (packetSrc.buttonNum === 0) InventoryAction.PLACE_ALL else InventoryAction.PLACE_ONE
//                                    }
//                                }
//                            } else if (slot.mayPickup(this.player)) {
//                                if (cursor.isEmpty()) {
//                                    if (slot.getItem().getItem() is BundleItem
//                                        && slot.getItem().has(DataComponents.BUNDLE_CONTENTS)
//                                        && packetSrc.buttonNum !== 0
//                                    ) {
//                                        action = if (slot.getItem().get(DataComponents.BUNDLE_CONTENTS).isEmpty())
//                                            InventoryAction.NOTHING
//                                        else
//                                            InventoryAction.PICKUP_FROM_BUNDLE
//                                    } else {
//                                        action = if (packetSrc.buttonNum === 0) InventoryAction.PICKUP_ALL else InventoryAction.PICKUP_HALF
//                                    }
//                                } else if (slot.mayPlace(cursor)) {
//                                    if (ItemStack.isSameItemSameComponents(clickedItem, cursor)) {
//                                        var toPlace = if (packetSrc.buttonNum === 0) cursor.getCount() else 1
//                                        toPlace = Math.min(toPlace, clickedItem.getMaxStackSize() - clickedItem.getCount())
//                                        toPlace = Math.min(toPlace, slot.container.getMaxStackSize() - clickedItem.getCount())
//                                        if (toPlace == 1) {
//                                            action = InventoryAction.PLACE_ONE
//                                        } else if (toPlace == cursor.getCount()) {
//                                            action = InventoryAction.PLACE_ALL
//                                        } else if (toPlace < 0) {
//                                            action = if (toPlace != -1) InventoryAction.PICKUP_SOME else InventoryAction.PICKUP_ONE
//                                        } else if (toPlace != 0) {
//                                            action = InventoryAction.PLACE_SOME
//                                        }
//                                    } else if (cursor.getCount() <= slot.getMaxStackSize()) {
//                                        if (cursor.getItem() is BundleItem && cursor.has(DataComponents.BUNDLE_CONTENTS) && packetSrc.buttonNum === 0) {
//                                            val toPickup: Int = cursor.get(DataComponents.BUNDLE_CONTENTS).getMaxAmountToAdd(slot.getItem())
//                                            if (toPickup >= slot.getItem().getCount()) {
//                                                action = InventoryAction.PICKUP_ALL_INTO_BUNDLE
//                                            } else if (toPickup == 0) {
//                                                action = InventoryAction.NOTHING
//                                            } else {
//                                                action = InventoryAction.PICKUP_SOME_INTO_BUNDLE
//                                            }
//                                        } else if (slot.getItem().getItem() is BundleItem
//                                            && slot.getItem().has(DataComponents.BUNDLE_CONTENTS)
//                                            && packetSrc.buttonNum === 0
//                                        ) {
//                                            val toPickup: Int = slot.getItem().get(DataComponents.BUNDLE_CONTENTS).getMaxAmountToAdd(cursor)
//                                            if (toPickup >= cursor.getCount()) {
//                                                action = InventoryAction.PLACE_ALL_INTO_BUNDLE
//                                            } else if (toPickup == 0) {
//                                                action = InventoryAction.NOTHING
//                                            } else {
//                                                action = InventoryAction.PLACE_SOME_INTO_BUNDLE
//                                            }
//                                        } else {
//                                            action = InventoryAction.SWAP_WITH_CURSOR
//                                        }
//                                    }
//                                } else if (ItemStack.isSameItemSameComponents(cursor, clickedItem)
//                                    && clickedItem.getCount() >= 0 && clickedItem.getCount() + cursor.getCount() <= cursor.getMaxStackSize()
//                                ) {
//                                    action = InventoryAction.PICKUP_ALL
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
////
////                        val inventory: InventoryView = this.player.containerMenu.getBukkitView()
////                        val type: SlotType? = inventory.getSlotType(slotNum)
////                        var click: ClickType? = ClickType.UNKNOWN
////                        var action: InventoryAction? = InventoryAction.UNKNOWN
////                        when (packet.clickType()) {
////                            PICKUP -> {
////                                if (packetSrc.buttonNum === 0) {
////                                    click = ClickType.LEFT
////                                } else if (packetSrc.buttonNum === 1) {
////                                    click = ClickType.RIGHT
////                                }
////
////                                if (packetSrc.buttonNum === 0 || packetSrc.buttonNum === 1) {
////                                    action = InventoryAction.NOTHING
////                                    if (slotNum == -999) {
////                                        if (!this.player.containerMenu.getCarried().isEmpty()) {
////                                            action = if (packetSrc.buttonNum === 0) InventoryAction.DROP_ALL_CURSOR else InventoryAction.DROP_ONE_CURSOR
////                                        }
////                                    } else if (slotNum < 0) {
////                                        action = InventoryAction.NOTHING
////                                    } else {
////                                        val slot: Slot? = this.player.containerMenu.getSlot(slotNum)
////                                        if (slot != null) {
////                                            val clickedItem: ItemStack = slot.getItem()
////                                            val cursor: ItemStack = this.player.containerMenu.getCarried()
////                                            if (clickedItem.isEmpty()) {
////                                                if (!cursor.isEmpty()) {
////                                                    if (cursor.getItem() is BundleItem && cursor.has(DataComponents.BUNDLE_CONTENTS) && packetSrc.buttonNum !== 0) {
////                                                        action = if (cursor.get(DataComponents.BUNDLE_CONTENTS).isEmpty())
////                                                            InventoryAction.NOTHING
////                                                        else
////                                                            InventoryAction.PLACE_FROM_BUNDLE
////                                                    } else {
////                                                        action = if (packetSrc.buttonNum === 0) InventoryAction.PLACE_ALL else InventoryAction.PLACE_ONE
////                                                    }
////                                                }
////                                            } else if (slot.mayPickup(this.player)) {
////                                                if (cursor.isEmpty()) {
////                                                    if (slot.getItem().getItem() is BundleItem
////                                                        && slot.getItem().has(DataComponents.BUNDLE_CONTENTS)
////                                                        && packetSrc.buttonNum !== 0
////                                                    ) {
////                                                        action = if (slot.getItem().get(DataComponents.BUNDLE_CONTENTS).isEmpty())
////                                                            InventoryAction.NOTHING
////                                                        else
////                                                            InventoryAction.PICKUP_FROM_BUNDLE
////                                                    } else {
////                                                        action = if (packetSrc.buttonNum === 0) InventoryAction.PICKUP_ALL else InventoryAction.PICKUP_HALF
////                                                    }
////                                                } else if (slot.mayPlace(cursor)) {
////                                                    if (ItemStack.isSameItemSameComponents(clickedItem, cursor)) {
////                                                        var toPlace = if (packetSrc.buttonNum === 0) cursor.getCount() else 1
////                                                        toPlace = Math.min(toPlace, clickedItem.getMaxStackSize() - clickedItem.getCount())
////                                                        toPlace = Math.min(toPlace, slot.container.getMaxStackSize() - clickedItem.getCount())
////                                                        if (toPlace == 1) {
////                                                            action = InventoryAction.PLACE_ONE
////                                                        } else if (toPlace == cursor.getCount()) {
////                                                            action = InventoryAction.PLACE_ALL
////                                                        } else if (toPlace < 0) {
////                                                            action = if (toPlace != -1) InventoryAction.PICKUP_SOME else InventoryAction.PICKUP_ONE
////                                                        } else if (toPlace != 0) {
////                                                            action = InventoryAction.PLACE_SOME
////                                                        }
////                                                    } else if (cursor.getCount() <= slot.getMaxStackSize()) {
////                                                        if (cursor.getItem() is BundleItem && cursor.has(DataComponents.BUNDLE_CONTENTS) && packetSrc.buttonNum === 0) {
////                                                            val toPickup: Int = cursor.get(DataComponents.BUNDLE_CONTENTS).getMaxAmountToAdd(slot.getItem())
////                                                            if (toPickup >= slot.getItem().getCount()) {
////                                                                action = InventoryAction.PICKUP_ALL_INTO_BUNDLE
////                                                            } else if (toPickup == 0) {
////                                                                action = InventoryAction.NOTHING
////                                                            } else {
////                                                                action = InventoryAction.PICKUP_SOME_INTO_BUNDLE
////                                                            }
////                                                        } else if (slot.getItem().getItem() is BundleItem
////                                                            && slot.getItem().has(DataComponents.BUNDLE_CONTENTS)
////                                                            && packetSrc.buttonNum === 0
////                                                        ) {
////                                                            val toPickup: Int = slot.getItem().get(DataComponents.BUNDLE_CONTENTS).getMaxAmountToAdd(cursor)
////                                                            if (toPickup >= cursor.getCount()) {
////                                                                action = InventoryAction.PLACE_ALL_INTO_BUNDLE
////                                                            } else if (toPickup == 0) {
////                                                                action = InventoryAction.NOTHING
////                                                            } else {
////                                                                action = InventoryAction.PLACE_SOME_INTO_BUNDLE
////                                                            }
////                                                        } else {
////                                                            action = InventoryAction.SWAP_WITH_CURSOR
////                                                        }
////                                                    }
////                                                } else if (ItemStack.isSameItemSameComponents(cursor, clickedItem)
////                                                    && clickedItem.getCount() >= 0 && clickedItem.getCount() + cursor.getCount() <= cursor.getMaxStackSize()
////                                                ) {
////                                                    action = InventoryAction.PICKUP_ALL
////                                                }
////                                            }
////                                        }
////                                    }
////                                }
////                            }
////
////                            QUICK_MOVE -> {
////                                if (packetSrc.buttonNum === 0) {
////                                    click = ClickType.SHIFT_LEFT
////                                } else if (packetSrc.buttonNum === 1) {
////                                    click = ClickType.SHIFT_RIGHT
////                                }
////
////                                if (packetSrc.buttonNum === 0 || packetSrc.buttonNum === 1) {
////                                    if (slotNum < 0) {
////                                        action = InventoryAction.NOTHING
////                                    } else {
////                                        val slot: Slot? = this.player.containerMenu.getSlot(slotNum)
////                                        if (slot != null && slot.mayPickup(this.player) && slot.hasItem()) {
////                                            action = InventoryAction.MOVE_TO_OTHER_INVENTORY
////                                        } else {
////                                            action = InventoryAction.NOTHING
////                                        }
////                                    }
////                                }
////                            }
////
////                            SWAP -> if (packetSrc.buttonNum >= 0 && packetSrc.buttonNum < 9 || packetSrc.buttonNum === 40) {
////                                if (slotNum < 0) {
////                                    action = InventoryAction.NOTHING
////                                } else {
////                                    click = if (packetSrc.buttonNum === 40) ClickType.SWAP_OFFHAND else ClickType.NUMBER_KEY
////                                    val clickedSlot: Slot = this.player.containerMenu.getSlot(slotNum)
////                                    if (clickedSlot.mayPickup(this.player)) {
////                                        val hotbar: ItemStack = this.player.getInventory().getItem(packetSrc.buttonNum)
////                                        if ((hotbar.isEmpty() || !clickedSlot.mayPlace(hotbar)) && (!hotbar.isEmpty() || !clickedSlot.hasItem())) {
////                                            action = InventoryAction.NOTHING
////                                        } else {
////                                            action = InventoryAction.HOTBAR_SWAP
////                                        }
////                                    } else {
////                                        action = InventoryAction.NOTHING
////                                    }
////                                }
////                            }
////
////                            CLONE -> if (packetSrc.buttonNum === 2) {
////                                click = ClickType.MIDDLE
////                                if (slotNum < 0) {
////                                    action = InventoryAction.NOTHING
////                                } else {
////                                    val slot: Slot? = this.player.containerMenu.getSlot(slotNum)
////                                    if (slot != null && slot.hasItem() && this.player.getAbilities().instabuild && this.player.containerMenu.getCarried()
////                                            .isEmpty()
////                                    ) {
////                                        action = InventoryAction.CLONE_STACK
////                                    } else {
////                                        action = InventoryAction.NOTHING
////                                    }
////                                }
////                            } else {
////                                click = ClickType.UNKNOWN
////                                action = InventoryAction.UNKNOWN
////                            }
////
////                            THROW -> if (slotNum >= 0) {
////                                if (packetSrc.buttonNum === 0) {
////                                    click = ClickType.DROP
////                                    val slot: Slot? = this.player.containerMenu.getSlot(slotNum)
////                                    if (slot != null && slot.hasItem()
////                                        && slot.mayPickup(this.player)
////                                        && !slot.getItem().isEmpty() && slot.getItem().getItem() !== Items.AIR
////                                    ) {
////                                        action = InventoryAction.DROP_ONE_SLOT
////                                    } else {
////                                        action = InventoryAction.NOTHING
////                                    }
////                                } else if (packetSrc.buttonNum === 1) {
////                                    click = ClickType.CONTROL_DROP
////                                    val slot: Slot? = this.player.containerMenu.getSlot(slotNum)
////                                    if (slot != null && slot.hasItem()
////                                        && slot.mayPickup(this.player)
////                                        && !slot.getItem().isEmpty() && slot.getItem().getItem() !== Items.AIR
////                                    ) {
////                                        action = InventoryAction.DROP_ALL_SLOT
////                                    } else {
////                                        action = InventoryAction.NOTHING
////                                    }
////                                }
////                            } else {
////                                click = ClickType.LEFT
////                                if (packetSrc.buttonNum === 1) {
////                                    click = ClickType.RIGHT
////                                }
////
////                                action = InventoryAction.NOTHING
////                            }
////
////                            QUICK_CRAFT -> {
////                                val containerMenu: AbstractContainerMenu = this.player.containerMenu
////                                val currentStatus: Int = this.player.containerMenu.quickcraftStatus
////                                val newStatus: Int = AbstractContainerMenu.getQuickcraftHeader(packetSrc.buttonNum)
////                                if (currentStatus == 1 && (newStatus == 2 || currentStatus == newStatus)
////                                    && !containerMenu.getCarried()
////                                        .isEmpty() && newStatus != 0 && newStatus != 1 && newStatus == 2 && !this.player.containerMenu.quickcraftSlots.isEmpty() && this.player.containerMenu.quickcraftSlots.size() === 1
////                                ) {
////                                    val index: Int = (containerMenu.quickcraftSlots.iterator().next() as Slot).index
////                                    containerMenu.resetQuickCraft()
////                                    this.handleContainerClick(
////                                        ServerboundContainerClickPacket(
////                                            packet.containerId(),
////                                            packet.stateId(),
////                                            index.toShort(),
////                                            containerMenu.quickcraftType as Byte,
////                                            net.minecraft.world.inventory.ClickType.PICKUP,
////                                            packet.changedSlots(),
////                                            packet.carriedItem()
////                                        )
////                                    )
////                                    return
////                                }
////
////                                this.player.containerMenu.clicked(slotNum, packetSrc.buttonNum, packet.clickType(), this.player)
////                            }
////
////                            PICKUP_ALL -> {
////                                click = ClickType.DOUBLE_CLICK
////                                action = InventoryAction.NOTHING
////                                if (slotNum >= 0 && !this.player.containerMenu.getCarried().isEmpty()) {
////                                    val cursor: ItemStack = this.player.containerMenu.getCarried()
////                                    action = InventoryAction.NOTHING
////                                    if (inventory.getTopInventory().contains(CraftItemType.minecraftToBukkit(cursor.getItem()))
////                                        || inventory.getBottomInventory().contains(CraftItemType.minecraftToBukkit(cursor.getItem()))
////                                    ) {
////                                        action = InventoryAction.COLLECT_TO_CURSOR
////                                    }
////                                }
////                            }
////                        }
////
////                        if (packet.clickType() !== net.minecraft.world.inventory.ClickType.QUICK_CRAFT) {
////                            var event: InventoryClickEvent?
////                            if (click === ClickType.NUMBER_KEY) {
////                                event = InventoryClickEvent(inventory, type, slotNum, click, action, packetSrc.buttonNum)
////                            } else {
////                                event = InventoryClickEvent(inventory, type, slotNum, click, action)
////                            }
////
////                            val top: org.bukkit.inventory.Inventory? = inventory.getTopInventory()
////                            if (slotNum == 0 && top is CraftingInventory) {
////                                val recipe: Recipe? = top.getRecipe()
////                                if (recipe != null) {
////                                    if (click === ClickType.NUMBER_KEY) {
////                                        event = CraftItemEvent(recipe, inventory, type, slotNum, click, action, packetSrc.buttonNum)
////                                    } else {
////                                        event = CraftItemEvent(recipe, inventory, type, slotNum, click, action)
////                                    }
////                                }
////                            }
////
////                            if (slotNum == 3 && top is SmithingInventory) {
////                                val result: org.bukkit.inventory.ItemStack? = top.getResult()
////                                if (result != null) {
////                                    if (click === ClickType.NUMBER_KEY) {
////                                        event = SmithItemEvent(inventory, type, slotNum, click, action, packetSrc.buttonNum)
////                                    } else {
////                                        event = SmithItemEvent(inventory, type, slotNum, click, action)
////                                    }
////                                }
////                            }
////
////                            if (slotNum == 2 && top is CartographyInventory) {
////                                val result: org.bukkit.inventory.ItemStack? = top.getResult()
////                                if (result != null && !result.isEmpty()) {
////                                    if (click === ClickType.NUMBER_KEY) {
////                                        event = CartographyItemEvent(inventory, type, slotNum, click, action, packetSrc.buttonNum)
////                                    } else {
////                                        event = CartographyItemEvent(inventory, type, slotNum, click, action)
////                                    }
////                                }
////                            }
////
////                            event.setCancelled(cancelled)
////                            val oldContainer: AbstractContainerMenu? = this.player.containerMenu
////                            super.cserver.getPluginManager().callEvent(event)
////                            if (this.player.containerMenu !== oldContainer) {
////                                this.player.containerMenu.resumeRemoteUpdates()
////                                this.player.containerMenu.broadcastFullState()
////                                return
////                            }
////
////                            if (event.getResult() !== Result.DENY) {
////                                this.player.containerMenu.clicked(slotNum, packetSrc.buttonNum, packet.clickType(), this.player)
////                            }
////
////                            if (event is CraftItemEvent || event is SmithItemEvent) {
////                                this.player.containerMenu.sendAllDataToRemote()
////                            }
////                        }
////
////                        val var21: ObjectIterator = Int2ObjectMaps.fastIterable(packet.changedSlots()).iterator()
////
////                        while (var21.hasNext()) {
////                            val entry: `Int2ObjectMap$Entry`<HashedStack?> = var21.next() as `Int2ObjectMap$Entry`<HashedStack?>
////                            this.player.containerMenu.setRemoteSlotUnsafe(entry.getIntKey(), entry.getValue() as HashedStack?)
////                        }
////
////                        this.player.containerMenu.setRemoteCarried(packet.carriedItem())
////                        this.player.containerMenu.resumeRemoteUpdates()
////                        if (flag) {
////                            this.player.containerMenu.broadcastFullState()
////                        } else {
////                            this.player.containerMenu.broadcastChanges()
////                        }
////
////                        if (packetSrc.buttonNum === 40 && this.player.containerMenu !== this.player.inventoryMenu) {
////                            this.player.containerSynchronizer.sendOffHandSlotChange()
////                        }
////
////                        if (GlobalConfiguration.get().unsupportedSettings.updateEquipmentOnPlayerActions) {
////                            this.player.detectEquipmentUpdates()
////                        }
////                    }
////                }
////            }
////        }
//}
//
//}