package today.vanta.client.screen;

import imgui.ImGui;
import net.minecraft.client.gui.GuiScreen;
import today.vanta.util.client.IClient;
import today.vanta.util.system.lwjgl.imgui.ImGuiImpl;

public class ImGuiClickGuiScreen extends GuiScreen implements IClient {
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ImGuiImpl.draw(() -> {
            if (ImGui.begin(CLIENT_NAME)) {
                
            }
            ImGui.end();
        });
    }
}