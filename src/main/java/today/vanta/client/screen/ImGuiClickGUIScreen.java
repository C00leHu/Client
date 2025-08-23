package today.vanta.client.screen;

import imgui.ImGui;
import net.minecraft.client.gui.GuiScreen;
import today.vanta.Vanta;
import today.vanta.client.module.Category;
import today.vanta.client.module.Module;
import today.vanta.client.setting.Setting;
import today.vanta.client.setting.impl.BooleanSetting;
import today.vanta.client.setting.impl.MultiStringSetting;
import today.vanta.client.setting.impl.NumberSetting;
import today.vanta.client.setting.impl.StringSetting;
import today.vanta.util.client.IClient;
import today.vanta.util.system.lwjgl.imgui.ImGuiImpl;

import java.util.*;

public class ImGuiClickGUIScreen extends GuiScreen implements IClient {
    private Category currentCategory = Category.COMBAT;
    private final Map<Category, Module> lastModulePerCategory = new EnumMap<>(Category.class);
    private Module currentModule;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ImGuiImpl.draw(() -> {
            ImGui.setNextWindowSize(600, 425);
            if (ImGui.begin(CLIENT_NAME)) {
                if (ImGui.beginChild("categories", 150, 0, true)) {
                    float fullWidth = ImGui.getContentRegionAvailX();

                    for (Category category : Category.values()) {
                        if (ImGui.button(category.name, fullWidth, 50)) {
                            if (currentModule != null) {
                                lastModulePerCategory.put(currentCategory, currentModule);
                            }

                            currentCategory = category;

                            currentModule = lastModulePerCategory.getOrDefault(category, null);
                        }
                    }

                    ImGui.endChild();
                }

                ImGui.sameLine();

                if (ImGui.beginChild("modules", 150, 0, true)) {
                    float fullWidth = ImGui.getContentRegionAvailX();
                    for (Module module : Vanta.instance.moduleStorage.getModulesByCategory(currentCategory)) {
                        if (ImGui.button(module.name, fullWidth, 20)) {
                            currentModule = module;
                            lastModulePerCategory.put(currentCategory, module);
                        }
                    }
                    ImGui.endChild();
                }

                ImGui.sameLine();

                if (currentModule != null) {
                    if (ImGui.beginChild("settings", 0, 0, true)) {
                        float fullWidth = ImGui.getContentRegionAvailX();

                        ImGui.text(currentModule.displayName);
                        ImGui.separator();

                        for (Setting<?> setting : currentModule.settings) {
                            if (setting.isHidden()) continue;
                            if (setting instanceof BooleanSetting) {
                                BooleanSetting boolSet = (BooleanSetting) setting;
                                String label = boolSet.name + ": " + (boolSet.getValue() ? "enabled" : "disabled");

                                if (ImGui.button(label, fullWidth, 20)) {
                                    boolSet.setValue(!boolSet.getValue());
                                }
                            } else if (setting instanceof NumberSetting) {
                                NumberSetting numSet = (NumberSetting) setting;
                                ImGui.text(numSet.name);
                                ImGui.sameLine();
                                float avail = ImGui.getContentRegionAvailX();

                                ImGui.pushItemWidth(avail);

                                if (numSet.getValue() instanceof Integer) {
                                    int[] val = {numSet.getValue().intValue()};
                                    if (ImGui.sliderInt("##" + numSet.name, val,
                                            numSet.min.intValue(),
                                            numSet.max.intValue())) {
                                        numSet.setValue(val[0]);
                                    }
                                } else {
                                    float[] val = {numSet.getValue().floatValue()};
                                    if (ImGui.sliderFloat("##" + numSet.name, val,
                                            numSet.min.floatValue(),
                                            numSet.max.floatValue(),
                                            "%." + numSet.places + "f")) {
                                        numSet.setValue(val[0]);
                                    }
                                }

                                ImGui.popItemWidth();
                            } else if (setting instanceof MultiStringSetting) {
                                MultiStringSetting multiSet = (MultiStringSetting) setting;

                                String label = multiSet.name + " (" + multiSet.getValue().length + " enabled)";

                                ImGui.pushItemWidth(fullWidth);

                                if (ImGui.beginCombo("##22" + label, label)) {
                                    for (String val : multiSet.allValues) {
                                        boolean isSelected = multiSet.isEnabled(val);
                                        if (ImGui.selectable(val, isSelected)) {
                                            List<String> list = new ArrayList<>(Arrays.asList(multiSet.getValue()));
                                            if (isSelected) {
                                                list.remove(val);
                                            } else {
                                                list.add(val);
                                            }
                                            multiSet.setValue(list.toArray(new String[0]));
                                        }
                                        if (isSelected) {
                                            ImGui.setItemDefaultFocus();
                                        }
                                    }
                                    ImGui.endCombo();
                                }

                                ImGui.popItemWidth();
                            } else if (setting instanceof StringSetting) {
                                StringSetting strSet = (StringSetting) setting;

                                ImGui.text(strSet.name);
                                ImGui.sameLine();
                                float avail = ImGui.getContentRegionAvailX();

                                ImGui.pushItemWidth(avail);

                                String[] items = strSet.allValues;

                                int currentIndex = strSet.index();
                                int[] selected = {currentIndex};
                                if (ImGui.beginCombo(strSet.name, items[currentIndex])) {
                                    for (int i = 0; i < items.length; i++) {
                                        boolean isSelected = (i == selected[0]);
                                        if (ImGui.selectable(items[i], isSelected)) {
                                            selected[0] = i;
                                            strSet.setValue(items[i]);
                                        }
                                        if (isSelected) {
                                            ImGui.setItemDefaultFocus();
                                        }
                                    }
                                    ImGui.endCombo();
                                }

                                ImGui.popItemWidth();
                            }
                        }

                        ImGui.endChild();
                    }
                }
            }
            ImGui.end();
        });
    }
}