package org.scrobotic.humbank.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.humbank.ktorclient.icons.imagevectors.CreditCard
import org.humbank.ktorclient.icons.imagevectors.FastFood
import org.jetbrains.compose.resources.painterResource
import org.scrobotic.humbank.ui.BlueEnd
import org.scrobotic.humbank.ui.BlueStart
import org.scrobotic.humbank.ui.GreenEnd
import org.scrobotic.humbank.ui.GreenStart
import org.scrobotic.humbank.ui.OrangeEnd
import org.scrobotic.humbank.ui.OrangeStart
import org.scrobotic.humbank.ui.PurpleEnd
import org.scrobotic.humbank.ui.PurpleStart


data class Business(
    val businessType: String,
    val businessOwner: String,
    val businessName: String,
    val businessRoom: String,
    val color: Brush // Assuming getGradient returns a Brush
)

val cards = listOf(
    Business(
        businessType = "FINANCE",
        businessOwner = "Patrick Guha & Cornelius Binder",
        businessName = "Humbank",
        businessRoom = "Bib",
        color = getGradient(PurpleStart, PurpleEnd)
    ),
    Business(
        businessType = "GASTRO",
        businessOwner = "Daniel Eberhardt",
        businessName = "BurgerYou",
        businessRoom = "H1",
        color = getGradient(BlueStart, BlueEnd)
    ),
)

fun getGradient(
    startColor: Color,
    endColor: Color,
): Brush {
    return Brush.horizontalGradient(
        colors = listOf(startColor, endColor)
    )
}

@Preview
@Composable
fun BusinessesSection() {
    LazyRow {
        items(cards.size) { index ->
            CardItem(index)
        }
    }
}

@Composable
fun CardItem(
    index: Int
) {
    val card = cards[index]
    var lastItemPaddingEnd = 0.dp
    if (index == cards.size - 1) {
        lastItemPaddingEnd = 16.dp
    }

    var image: Painter = rememberVectorPainter(CreditCard)
    if (card.businessType == "GASTRO") {
        image = rememberVectorPainter(FastFood)
    }

    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = lastItemPaddingEnd)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(card.color)
                .width(250.dp)
                .height(160.dp)
                .clickable {}
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Image(
                painter = image,
                contentDescription = card.businessName,
                modifier = Modifier.width(60.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = card.businessName,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = card.businessRoom,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = card.businessOwner,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

        }
    }
}