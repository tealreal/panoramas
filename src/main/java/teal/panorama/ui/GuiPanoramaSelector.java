package teal.panorama.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
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
        this.searchBox = new TextFieldWidget(font, this.width / 2 - 148, this.height / 2 + 80, 70, 20, null, I18n.translate("panorama.gui.search"));
        this.children.add(this.searchBox);
        this.focusOn(this.searchBox);
        this.addButton(new ButtonWidget(this.width / 2 - 72, this.height / 2 + 80, 46, 20, I18n.translate("panorama.gui.menu.search"), (b) -> {
            if (this.searchBox.getText() != null && !this.searchBox.getText().isEmpty()) {
                this.panoramas = PanoramaRegistry.getAllForName(this.searchBox.getText());
            } else {
                this.panoramas = PanoramaRegistry.PANORAMAS;
            }
            this.page = 0;
            this.btnNextPage.active = this.panoramas.size() > 8;
            this.refreshButtons();
        }));
        this.btnPreviousPage = this.addButton(new ButtonWidget(this.width / 2 - 22, this.height / 2 + 80, 80, 20, I18n.translate("panorama.gui.menu.prevpage"), (b) -> {
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
        this.btnNextPage = this.addButton(new ButtonWidget(this.width / 2 + 62, this.height / 2 + 80, 80, 20, I18n.translate("panorama.gui.menu.nextpage"), (b) -> {
            if ((this.page + 1) * 8 < this.panoramas.size()) {
                ++this.page;
                this.btnPreviousPage.active = true;
                this.refreshButtons();
                if ((this.page + 1) * 8 >= this.panoramas.size()) {
                    this.btnNextPage.active = false;
                }
            }

        }));
        this.addButton(new ButtonWidget(4, 4, 60, 20, I18n.translate("gui.back"), (b) -> {
            if (MinecraftClient.getInstance().world == null) {
                MinecraftClient.getInstance().openScreen(new TitleScreen());
            } else {
                MinecraftClient.getInstance().openScreen(new GameMenuScreen(true));
            }
        }));

        this.addButton(new ResolutionButton(70, 4, 80, 20));

        this.addButton(new ButtonWidget(this.width - 64, 4, 60, 20, I18n.translate("panorama.gui.menu.reload"), (b) -> {
            this.page = 0;
            PanoramaRegistry.setup();
            this.panoramas = PanoramaRegistry.PANORAMAS;
            this.btnNextPage.active = this.panoramas.size() > 8;

            this.refreshButtons();
        }));
        this.addButton(new ResetButton(this.width - 128, 4, 60, 20));
        if (this.panoramas.size() <= 8) {
            this.btnNextPage.active = false;
        }

        this.refreshButtons();
    }

    @Nullable
    public String getSizeWarning() {
        return Config.INSTANCE.save_resolution.res >= 8192 ? "§c" + I18n.translate("panorama.gui.menu.resolution.warning") : null;
    }

    public void refreshButtons() {
        int i;
        for (i = 0; i < this.panoButtons.size(); ++i) {
            PanoramaButton b = this.panoButtons.get(i);
            this.buttons.remove(b);
            this.children.remove(b);
        }

        for (i = this.page * 8; i < this.page * 8 + 8; ++i) {
            if (i < this.panoramas.size()) {
                PanoramaInstance pan = this.panoramas.get(i);
                this.addPanoramaButton(new PanoramaButton(pan, this.width / 2 - 148 + i % 4 * 74, this.height / 2 + i % 8 / 4 * 74 - 74));
            }
        }

    }

    public void addPanoramaButton(PanoramaButton btn) {
        this.addButton(btn);
        this.panoButtons.add(btn);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        Main.SKYBOX.render(partialTicks, MathHelper.clamp(1.0F, 0.0F, 1.0F));
        drawCenteredString(font, I18n.translate("panorama.gui.menu.resolution"), 110, 26, -1052689);
        drawCenteredString(font, I18n.translate("panorama.gui.menu.selector"), this.width / 2, this.height / 2 - 102, -1);
        if (this.panoramas.isEmpty()) {
            drawCenteredString(font, I18n.translate("panorama.gui.menu.nosearchresults"), this.width / 2, this.height / 2, -1);
        }

        if (this.searchBox != null) {
            this.searchBox.render(mouseX, mouseY, partialTicks);
        }

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        if (this.searchBox != null) {
            this.searchBox.tick();
        }

        super.tick();
    }

    class ResetButton extends ButtonWidget {
        public ResetButton(int x, int y, int width, int height) {
            super(x, y, width, height, I18n.translate("controls.reset"), (b) -> {
                try {
                    Util.loadPack("");
                } catch (Exception e) {
                    Main.logger.error(e.getLocalizedMessage());
                }
            });
        }

        @Override
        public void renderToolTip(int mouseX, int mouseY) {
            GuiPanoramaSelector.this.renderTooltip(I18n.translate("panorama.gui.menu.reset.tooltip"), mouseX, mouseY);
        }
    }

    class ResolutionButton extends ButtonWidget {
        public ResolutionButton(int x, int y, int width, int height) {
            super(x, y, width, height, Config.INSTANCE.save_resolution.name, (b) -> {
                int order = Config.INSTANCE.save_resolution.ordinal() + 1;
                Config.INSTANCE.save_resolution = Main.CaptureResolution.rs[order % Main.CaptureResolution.rs.length];
                b.setMessage(Config.INSTANCE.save_resolution.name);
                Config.save();
            });
        }

        @Override
        public void renderToolTip(int mouseX, int mouseY) {
            String tooltip = GuiPanoramaSelector.this.getSizeWarning();
            if (tooltip != null) GuiPanoramaSelector.this.renderTooltip(tooltip, mouseX, mouseY);
        }
    }

    public class PanoramaButton extends ButtonWidget {
        public static Identifier BORDER = new Identifier("panorama", "textures/gui/border.png");
        private final NativeImageBackedTexture icon;
        private final PanoramaInstance panorama;

        public PanoramaButton(PanoramaInstance panorama, int x, int y) {
            super(x, y, 64, 64, "", null);
            this.panorama = panorama;
            this.icon = panorama.getIcon();
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTicks) {
            super.render(mouseX, mouseY, partialTicks);
            icon.bindTexture();
            RenderSystem.enableDepthTest();
            blit(this.x, this.y, 0.0F, 0.0F, 64, 64, 64, 64);
            MinecraftClient.getInstance().getTextureManager().bindTexture(BORDER);
            blit(this.x - 2, this.y - 2, 0.0F, 0.0F, 70, 70, 70, 70);
        }

        @Override
        public void onPress() {
            try {
                Util.loadPack(this.panorama.getPanoramaName());
            } catch (Exception e) {
                Main.logger.error(e.getLocalizedMessage());
            }
        }

        public PanoramaInstance getPanorama() {
            return this.panorama;
        }

        @Override
        public void renderToolTip(int mouseX, int mouseY) {
            GuiPanoramaSelector.this.renderTooltip(panorama.getPanoramaName(), mouseX, mouseY);
        }
    }
}
