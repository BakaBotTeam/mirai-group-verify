package ltd.guimc.mirai.groupverify.utils

import ltd.guimc.mirai.groupverify.config.Config
import ltd.guimc.mirai.groupverify.utils.HashUtils.sha256
import net.mamoe.mirai.contact.Member
import org.json.JSONObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object APIUtils {
    val httpclient = HttpClient.newBuilder().build();

    fun newSession(member: Member): String {
        val time = System.currentTimeMillis()
        val url = "${Config.backendUrl}?action=new&appid=${Config.appid}&qqid=${member.id}&group=${member.group.id}&time=${time}&sign=${sign(member, time)}"
        return JSONObject(get(url)).getString("session")!!
    }

    fun getStatus(session: String): Int {
        val url = "${Config.backendUrl}?action=status&session=$session"
        val success = JSONObject(get(url)).getBoolean("success")
        val timedout = JSONObject(get(url)).getBoolean("timeout")

        return if (success) {
            Status.FINISHED
        } else if (timedout) {
            Status.TIMEDOUT
        } else {
            Status.INPROGRESS
        }
    }

    fun get(url: String): String {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build();

        val response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body()!!
    }

    object Status {
        const val INPROGRESS = 0
        const val FINISHED = 1
        const val TIMEDOUT = 2
    }

    private fun sign(member: Member, time: Long): String = "${Config.appid}:$secret:${member.group.id}:${member.id}:$time".sha256()
    private val secret: String
        get() = "${Config.appid}:${Config.secret}".sha256()
}