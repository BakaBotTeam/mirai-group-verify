package ltd.guimc.mirai.groupverify.listener

import kotlinx.coroutines.delay
import ltd.guimc.mirai.groupverify.PluginMain
import ltd.guimc.mirai.groupverify.config.Config
import ltd.guimc.mirai.groupverify.utils.APIUtils
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.contact.NormalMember
import net.mamoe.mirai.contact.checkBotPermission
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText

object MemberListener {
    suspend fun onEvent(event: MemberJoinEvent) {
        if (event.group.permitteeId.hasPermission(PluginMain.enablePerm)) {
            event.group.checkBotPermission(MemberPermission.ADMINISTRATOR) { "权限不足" }
            event.member.mute(2592000) // 30 Days

            val session = APIUtils.newSession(event.member)
            event.group.sendMessage(
                At(event.member)+
                PlainText(" 你好呀! 进入本群,你需要先完成以下的人机验证!\n" +
                    "请点击下面的链接来开始验证\n\n" +
                    "${Config.backendUrl}?session=$session")
            )
            statusListenerRegister(event.member, session)
        }
    }

    private suspend fun statusListenerRegister(member: Member, session: String) {
        while (!statusTimerTask(member, session)) {
            delay(2000)
        }
    }

    private suspend fun statusTimerTask(member: Member, session: String): Boolean {
        val status = APIUtils.getStatus(session)
        if (status == 1) {
            member.group.sendMessage(
                At(member)+
                PlainText(" 你已经完成了验证 现在可以正常发言了!")
            )
            (member as NormalMember).unmute()
            return true
        } else if (status == 2) {
            member.group.sendMessage(
                At(member)+
                PlainText(" 你的验证超时 请重新进群")
            )
            (member as NormalMember).kick("人机验证超时")
            return true
        }
        return false
    }
}