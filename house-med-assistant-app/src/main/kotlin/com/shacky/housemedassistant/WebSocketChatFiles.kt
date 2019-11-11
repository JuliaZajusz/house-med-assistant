package com.shacky.housemedassistant

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.shacky.housemedassistant.resolvers.SalesmanSetMutationResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.atomic.AtomicLong


class User(val id: Long, val name: String)
class Message(val msgType: String, val data: Any)

//@Component
class ChatHandler(val salesmanSetMutationResolver: SalesmanSetMutationResolver) : TextWebSocketHandler() {

    val sessionList = HashMap<WebSocketSession, User>()
    var uids = AtomicLong(0)

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionList -= session
    }

    //	public override fun handleTextMessage(session: WebSocketSession?, message: TextMessage?) {
    public override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
//	fun handleTextMessage(session: WebSocketSession?, message: TextMessage?) {
        val json = ObjectMapper().readTree(message?.payload)
        // {type: "join/say", data: "name/msg"}
        when (json.get("type").asText()) {
            "join" -> {
                val user = User(uids.getAndIncrement(), json.get("data").asText())
                sessionList.put(session!!, user)
                // tell this user about all other users
                emit(session, Message("users", sessionList.values))
                // tell all other users, about this user
                broadcastToOthers(session, Message("join", user))
            }
            "say" -> {
                broadcast(Message("say", json.get("data").asText()))
            }
            "jul" -> {
                val dataId = json.get("data")
                if (dataId != null) {
                    val id = dataId.asText();
//                val salesmanSetUtils = SalesmanSetUtils();
                    val salesmanSetA = salesmanSetMutationResolver.upgradeSalesmanSet(id, 5);
                    var s: String = "";
                    salesmanSetA.paths[0].places.map { patient -> patient.lastName }
                            .forEach { s += ", " + it }
                    println("UPDATE, najlepsza sciezka: ${salesmanSetA.paths[0].value}: $s")

                    println("!!!!!!!!!" + salesmanSetA.paths[0].value)
                    emit(session, Message("value", salesmanSetA))
                }
            }
        }
    }

    fun emit(session: WebSocketSession, msg: Message) = session.sendMessage(TextMessage(jacksonObjectMapper().writeValueAsString(msg)))
    fun broadcast(msg: Message) = sessionList.forEach { emit(it.key, msg) }
    fun broadcastToOthers(me: WebSocketSession, msg: Message) = sessionList.filterNot { it.key == me }.forEach { emit(it.key, msg) }
}

@Configuration
@EnableWebSocket
class WSConfig(val salesmanSetMutationResolver: SalesmanSetMutationResolver) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(ChatHandler(salesmanSetMutationResolver), "/chat").setAllowedOrigins("*").withSockJS()
    }
}
