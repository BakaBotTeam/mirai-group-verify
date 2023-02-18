package ltd.guimc.mirai.groupverify.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("config") {
    var backendUrl by value("http://example.com:5566/verify")

    var secret by value("xxx")

    var appid by value("xxx_xxx_xxxxxxxxxxxx")
}