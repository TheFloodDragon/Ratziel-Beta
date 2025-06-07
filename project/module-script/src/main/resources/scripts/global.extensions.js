
org.bukkit.Player.prototype.tell = function (message) {
    let adventure = Java.type("cn.fd.ratziel.common.message.AdventureBukkitKt");
    adventure.tell(message);
}
