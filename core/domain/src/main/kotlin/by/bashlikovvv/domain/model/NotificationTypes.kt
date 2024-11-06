package by.bashlikovvv.domain.model

sealed class NotificationTypes {
    class AppAlerts : NotificationTypes()
    class IncomingCall : NotificationTypes()
    class IncomingSms : NotificationTypes()
    class GoalNotification : NotificationTypes()
    class Alarm : NotificationTypes()
    class IdleAlerts : NotificationTypes()
    class EventReminder : NotificationTypes()
    class FindBand : NotificationTypes()
    class TodoList : NotificationTypes()
    class Schedule : NotificationTypes()
}