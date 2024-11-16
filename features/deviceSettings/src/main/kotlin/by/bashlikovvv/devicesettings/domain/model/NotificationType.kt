package by.bashlikovvv.devicesettings.domain.model

import android.os.Parcelable
import androidx.compose.runtime.Composable
import by.bashlikovvv.domain.model.NotificationTypes
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class NotificationType(val id: Int) : NotificationTypeCompose, Parcelable {
    fun toDomainNotificationType(): NotificationTypes =
        when(this) {
            is Alarm -> NotificationTypes.Alarm()
            is AppAlerts -> NotificationTypes.AppAlerts()
            is EventReminder -> NotificationTypes.EventReminder()
            is FindBand -> NotificationTypes.FindBand()
            is GoalNotification -> NotificationTypes.GoalNotification()
            is IdleAlerts -> NotificationTypes.IdleAlerts()
            is IncomingCall -> NotificationTypes.IncomingCall()
            is IncomingSms -> NotificationTypes.IncomingSms()
            is Schedule -> NotificationTypes.Schedule()
            is TodoList -> NotificationTypes.TodoList()
        }

    @Parcelize
    data object AppAlerts : NotificationType(0) {
        override val name: String @Composable get() = "AppAlerts"
    }

    @Parcelize
    data object IncomingCall : NotificationType(1) {
        override val name: String @Composable get() = "IncomingCall"
    }

    @Parcelize
    data object IncomingSms : NotificationType(2) {
        override val name: String @Composable get() = "IncomingSms"
    }

    @Parcelize
    data object GoalNotification : NotificationType(3) {
        override val name: String @Composable get() = "GoalNotification"
    }

    @Parcelize
    data object Alarm : NotificationType(4) {
        override val name: String @Composable get() = "Alarm"
    }

    @Parcelize
    data object IdleAlerts : NotificationType(5) {
        override val name: String @Composable get() = "IdleAlerts"
    }

    @Parcelize
    data object EventReminder : NotificationType(6) {
        override val name: String @Composable get() = "EventReminder"
    }

    @Parcelize
    data object FindBand : NotificationType(7) {
        override val name: String @Composable get() = "FindBand"
    }

    @Parcelize
    data object TodoList : NotificationType(8) {
        override val name: String @Composable get() = "TodoList"
    }

    @Parcelize
    data object Schedule : NotificationType(9) {
        override val name: String @Composable get() = "Schedule"
    }

    companion object {
        val entries: List<NotificationType>
            get() = listOf(
                AppAlerts,
                IncomingCall,
                IncomingSms,
                GoalNotification,
                Alarm,
                IdleAlerts,
                EventReminder,
                FindBand,
                TodoList,
                Schedule,
            )
    }
}