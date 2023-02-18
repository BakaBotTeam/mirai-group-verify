package ltd.guimc.mirai.groupverify

import ltd.guimc.mirai.groupverify.config.Config
import ltd.guimc.mirai.groupverify.listener.MemberListener
import net.mamoe.mirai.console.permission.Permission
import net.mamoe.mirai.console.permission.PermissionId
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.globalEventChannel

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "ltd.guimc.mirai.groupverify",
        name = "Mirai Group Verify",
        version = "0.1.0"
    ) {
        author("BakaBotTeam")
    }
) {
    lateinit var rootPerm: Permission
    lateinit var enablePerm: Permission

    override fun onEnable() {
        logger.info("Mirai Group Verify 正在加载的路上了喵~")
        registerPerms()
        registerEvents()
        Config.reload()
        logger.info("Mirai Group Verify 加载好啦喵~")
    }

    override fun onDisable() {
        logger.info("Mirai Group Verify 正在关闭呢...")
        Config.save()
        logger.info("Mirai Group Verify 已关闭了喵")
    }

    private fun registerPerms() = PermissionService.INSTANCE.run {
        rootPerm = register(PermissionId("ltd.guimc.mirai.groupverify", "*"), "Root Permission")
        enablePerm = register(PermissionId("ltd.guimc.mirai.groupverify", "enable"), "启用插件 (群)")
    }

    private fun registerEvents() = this.globalEventChannel().run {
        subscribeAlways<MemberJoinEvent> { event -> MemberListener.onEvent(event) }
    }
}
