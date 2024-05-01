package cn.fd.ratziel.function.argument

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer

/**
 * PlayerArgument
 *
 * @author TheFloodDragon
 * @since 2024/5/1 18:24
 */
class PlayerArgument(value: ProxyPlayer) : SingleArgument<ProxyPlayer>(value)

/**
 * CommandSenderArgument
 *
 * @author TheFloodDragon
 * @since 2024/5/1 18:27
 */
class CommandSenderArgument(value: ProxyCommandSender) : SingleArgument<ProxyCommandSender>(value)