package net.hacks.client.gui;

import net.hacks.client.HacksClient;
import net.hacks.client.module.Hack;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class HackScreen extends Screen {
    private final Screen parent;
    private final Map<ButtonWidget, Class<? extends Hack>> toggleableHackButtons = new HashMap<>();

    public HackScreen(Screen parent) {
        super(Text.of("hacks.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().marginX(5).marginBottom(4).alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(2);

        for (var hack : HacksClient.getHacks()) {
            this.toggleableHackButtons.put(adder.add(ButtonWidget.builder(
                            Text.empty(),
                            button -> HacksClient.toggle(hack.getClass())
                    ).build()),
                    hack.getClass()
            );
        }
        adder.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.client.setScreen(this.parent)).width(200).build(), 2, adder.copyPositioner().marginTop(6));
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, this.height / 6 - 12, this.width, this.height, 0.5f, 0.0f);
        gridWidget.forEachChild(this::addDrawableChild);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        for (var entry : this.toggleableHackButtons.entrySet()) {
            var button = entry.getKey();
            var clazz = entry.getValue();
            button.setMessage(buttonText(clazz));
        }
        super.render(context, mouseX, mouseY, delta);
    }

    private Text buttonText(Class<? extends Hack> hakk) {
        var classText = Text.translatable(hakk.getSimpleName()).append(" ");
        var isText = Text.translatable("hacks.is").append(" ");
        var enabledText = Text.translatable(HacksClient.isEnabled(hakk) ? "hacks.enabled" : "hacks.disabled");
        var text = classText.append(isText).append(enabledText);
        return text;
    }

}
