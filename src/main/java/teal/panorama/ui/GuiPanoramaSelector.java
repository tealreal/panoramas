package teal.panorama.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import teal.panorama.Config;
import teal.panorama.Main;
import teal.panorama.registry.PanoramaInstance;
import teal.panorama.registry.PanoramaRegistry;
import teal.panorama.util.Util;

import java.util.ArrayList;
import java.util.List;

public class GuiPanoramaSelector extends Screen {
    private final List<PanoramaButton> panoButtons = new ArrayList<>();
    private TextFieldWidget searchBox;
    private List<PanoramaInstance> panoramas;
    private ButtonWidget btnPreviousPage;
    private ButtonWidget btnNextPage;
    private int page = 0;

    public GuiPanoramaSelector() {
        super(new TranslatableText("panorama.gui.menu.main"));
        this.panoramas = PanoramaRegistry.PANORAMAS;
    }

    @Override
    protected void init() {
        super.init();
        this.page = 0;
        this.searchBox = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 148, this.height / 2 + 80, 70, 20, null, new TranslatableText("panorama.gui.search"));
        this.addDrawableChild(this.searchBox);
        this.focusOn(this.searchBox);
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 72, this.height / 2 + 80, 46, 20, new TranslatableText("panorama.gui.menu.search"), (b) -> {
            if (this.searchBox.getText() != null && !this.searchBox.getText().isEmpty()) {
                this.panoramas = PanoramaRegistry.getAllForName(this.searchBox.getText());
            } else {
                this.panoramas = PanoramaRegistry.PANORAMAS;
            }
            this.page = 0;
            this.btnNextPage.active = this.panoramas.size() > 8;
            this.refreshButtons();
        }));
        this.btnPreviousPage = this.addDrawableChild(new ButtonWidget(this.width / 2 - 22, this.height / 2 + 80, 80, 20, new TranslatableText("panorama.gui.menu.prevpage"), (b) -> {
            if (this.page - 1 >= 0) {
                --this.page;
                this.btnNextPage.active = true;
                this.refreshButtons();
                if (this.page - 1 < 0) {
                    this.btnPreviousPage.active = false;
                }
            }
        }));
        this.btnPreviousPage.active = false;
        this.btnNextPage = this.addDrawableChild(new ButtonWidget(this.width / 2 + 62, this.height / 2 + 80, 80, 20, new TranslatableText("panorama.gui.menu.nextpage"), (b) -> {
            if ((this.page + 1) * 8 < this.panoramas.size()) {
                ++this.page;
                this.btnPreviousPage.active = true;
                this.refreshButtons();
                if ((this.page + 1) * 8 >= this.panoramas.size()) {
                    this.btnNextPage.active = false;
                }
            }

        }));
        this.addDrawableChild(new ButtonWidget(4, 4, 60, 20, new TranslatableText("gui.back"), (b) -> {
            if (MinecraftClient.getInstance().world == null) {
                MinecraftClient.getInstance().setScreen(new TitleScreen());
            } else {
                MinecraftClient.getInstance().setScreen(new GameMenuScreen(true));
            }
        }));

        this.addDrawableChild(new ButtonWidget(70, 4, 80, 20, Config.INSTANCE.save_resolution.name, (b) -> {
            int order = Config.INSTANCE.save_resolution.ordinal() + 1;
            Config.INSTANCE.save_resolution = Main.CaptureResolution.rs[order % Main.CaptureResolution.rs.length];
            b.setMessage(Config.INSTANCE.save_resolution.name);
            Config.save();
        }, (button, matrices, mouseX, mouseY) -> {
            Text tooltip = getSizeWarning();
            if (tooltip != null) GuiPanoramaSelector.this.renderTooltip(matrices, tooltip, mouseX, mouseY);
        }));

        this.addDrawableChild(new ButtonWidget(this.width - 64, 4, 60, 20, new TranslatableText("panorama.gui.menu.reload"), (b) -> {
            this.page = 0;
            PanoramaRegistry.setup();
            this.panoramas = PanoramaRegistry.PANORAMAS;
            this.btnNextPage.active = this.panoramas.size() > 8;

            this.refreshButtons();
        }));
        this.addDrawableChild(new ButtonWidget(this.width - 128, 4, 60, 20, new TranslatableText("controls.reset"), (b) -> {
            try {
                Util.loadPack("");
            } catch (Exception e) {
                Main.logger.error(e.getLocalizedMessage());
            }
        }, ((button, matrices, mouseX, mouseY) -> GuiPanoramaSelector.this.renderTooltip(matrices, new TranslatableText("panorama.gui.menu.reset.tooltip"), mouseX, mouseY))));
        if (this.panoramas.size() <= 8) {
            this.btnNextPage.active = false;
        }

        this.refreshButtons();
    }

    @Nullable
    public Text getSizeWarning() {
        return Config.INSTANCE.save_resolution.res >= 8192 ? new TranslatableText("panorama.gui.menu.resolution.warning").formatted(Formatting.RED) : null;
    }

    public void refreshButtons() {
        int i;
        for (i = 0; i < this.panoButtons.size(); ++i) {
            PanoramaButton b = this.panoButtons.get(i);
            this.remove(b);
        }

        for (i = this.page * 8; i < this.page * 8 + 8; ++i) {
            if (i < this.panoramas.size()) {
                PanoramaInstance pan = this.panoramas.get(i);
                this.addPanoramaButton(new PanoramaButton(pan, this.width / 2 - 148 + i % 4 * 74, this.height / 2 + i % 8 / 4 * 74 - 74, ((button, matrices, mouseX, mouseY) -> GuiPanoramaSelector.this.renderTooltip(matrices, Text.of(pan.getPanoramaName()), mouseX, mouseY))));
            }
        }

    }

    public void addPanoramaButton(PanoramaButton btn) {
        this.addDrawableChild(btn);
        this.panoButtons.add(btn);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        Main.SKYBOX.render(partialTicks, MathHelper.clamp(1.0F, 0.0F, 1.0F));
        drawCenteredText(matrices, textRenderer, new TranslatableText("panorama.gui.menu.resolution"), 110, 26, -1052689);
        drawCenteredText(matrices, textRenderer, new TranslatableText("panorama.gui.menu.selector"), this.width / 2, this.height / 2 - 102, -1);
        if (this.panoramas.isEmpty()) {
            drawCenteredText(matrices, textRenderer, new TranslatableText("panorama.gui.menu.nosearchresults"), this.width / 2, this.height / 2, -1);
        }

        if (this.searchBox != null) {
            this.searchBox.render(matrices, mouseX, mouseY, partialTicks);
        }

        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        if (this.searchBox != null) {
            this.searchBox.tick();
        }

        super.tick();
    }
}
