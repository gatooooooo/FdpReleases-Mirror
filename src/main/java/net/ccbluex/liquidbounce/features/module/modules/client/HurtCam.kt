/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.network.play.server.S19PacketEntityStatus
import java.awt.Color

@ModuleInfo(name = "HurtCam", category = ModuleCategory.CLIENT,canEnable = false)
class HurtCam : Module() {
    val modeValue=ListValue("Mode", arrayOf("Vanilla","Cancel","FPS"),"Vanilla")
    private val colorRedValue = IntegerValue("R", 255, 0, 255).displayable { modeValue.get().equals("FPS",true) }
    private val colorGreenValue = IntegerValue("G", 0, 0, 255).displayable { modeValue.get().equals("FPS",true) }
    private val colorBlueValue = IntegerValue("B", 0, 0, 255).displayable { modeValue.get().equals("FPS",true) }
    private val colorRainbow = BoolValue("Rainbow", false).displayable { modeValue.get().equals("FPS",true) }
    private val timeValue = IntegerValue("FPSTime", 1000, 0, 1500).displayable { modeValue.get().equals("FPS",true) }
    private val fpsHeightValue = IntegerValue("FPSHeight", 25, 10, 50).displayable { modeValue.get().equals("FPS",true) }

    private var hurt=0L

    @EventTarget
    fun onRender2d(event: Render2DEvent){
        if(hurt==0L) return

        val passedTime=System.currentTimeMillis()-hurt
        if(passedTime>timeValue.get()){
            hurt=0L
            return
        }

        val color=getColor((((timeValue.get()-passedTime)/timeValue.get().toFloat())*255).toInt())
        val color1=getColor(0)
        val scaledResolution = ScaledResolution(mc)
        val width = scaledResolution.scaledWidth_double
        val height = scaledResolution.scaledHeight_double

        RenderUtils.drawGradientSidewaysV(0.0, 0.0, width, fpsHeightValue.get().toDouble(),color1.rgb,color.rgb)
        RenderUtils.drawGradientSidewaysV(0.0, height - fpsHeightValue.get(), width, height,color.rgb, color1.rgb)
    }

    @EventTarget
    fun onPacket(event: PacketEvent){
        val packet=event.packet

        when(modeValue.get().toLowerCase()){
            "fps" -> {
                if(packet is S19PacketEntityStatus){
                    if(packet.opCode.toInt()==2&&packet.getEntity(mc.theWorld).equals(mc.thePlayer)){
                        hurt=System.currentTimeMillis()
                    }
                }
            }
        }
    }

    private fun getColor(alpha: Int):Color{
        return if (colorRainbow.get()) ColorUtils.reAlpha(ColorUtils.rainbow(),alpha) else Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), alpha)
    }

    //always handle event
    override fun handleEvents() = true
}
