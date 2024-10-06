package by.bashlikovvv.healthrecoveryassistant.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import by.bashlikovvv.root.presentation.ui.RootContent
import by.bashlikovvv.root.presentation.ui.component.DefaultRootComponent
import by.bashlikovvv.root.presentation.ui.component.RootComponent
import by.bashlikovvv.root.presentation.ui.store.RootStore
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory

class MainActivity : ComponentActivity() {
    private val component: RootComponent by lazy {
        DefaultRootComponent(
            componentContext = defaultComponentContext(),
            storeFactory = DefaultStoreFactory()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        component.dispatchIntent(RootStore.Intent.Initialize(this))
        component.dispatchIntent(RootStore.Intent.OnNewIntent(intent))
        setContent {
            RootContent(component = component)
        }
    }

    override fun onNewIntent(intent: Intent) {
        component.dispatchIntent(RootStore.Intent.OnNewIntent(intent))
        super.onNewIntent(intent)
    }

    override fun onDestroy() {
        component.dispatchIntent(RootStore.Intent.Destroy)
        super.onDestroy()
    }
}