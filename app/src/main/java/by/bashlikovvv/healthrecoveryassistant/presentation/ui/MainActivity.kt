package by.bashlikovvv.healthrecoveryassistant.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import by.bashlikovvv.root.presentation.ui.RootContent
import by.bashlikovvv.root.presentation.ui.component.DefaultRootComponent
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RootContent(
                component = DefaultRootComponent(
                    componentContext = defaultComponentContext(),
                    storeFactory = DefaultStoreFactory()
                )
            )
        }
    }
}