package dev.huskuraft.effortless.screen.constraint;

import java.util.ArrayList;

import dev.huskuraft.effortless.EffortlessClient;
import dev.huskuraft.effortless.EffortlessConfigStorage;
import dev.huskuraft.effortless.api.gui.AbstractPanelScreen;
import dev.huskuraft.effortless.api.gui.button.Button;
import dev.huskuraft.effortless.api.gui.text.TextWidget;
import dev.huskuraft.effortless.api.gui.tooltip.TooltipHelper;
import dev.huskuraft.effortless.api.input.Keys;
import dev.huskuraft.effortless.api.lang.Lang;
import dev.huskuraft.effortless.api.platform.Entrance;
import dev.huskuraft.effortless.api.text.ChatFormatting;
import dev.huskuraft.effortless.api.text.Text;
import dev.huskuraft.effortless.networking.packets.player.PlayerPermissionCheckPacket;
import dev.huskuraft.effortless.screen.settings.EffortlessNotAnOperatorScreen;
import dev.huskuraft.effortless.screen.settings.EffortlessSessionStatusScreen;

public class EffortlessConstraintSettingsScreen extends AbstractPanelScreen {

    public EffortlessConstraintSettingsScreen(Entrance entrance) {
        super(entrance, Text.translate("effortless.constraint_settings.title"), PANEL_WIDTH, PANEL_TITLE_HEIGHT_1 + PANEL_BUTTON_ROW_HEIGHT_3);
    }

    private Button globalButton;
    private Button playerButton;

    @Override
    public void onCreate() {
        addWidget(new TextWidget(getEntrance(), getLeft() + getWidth() / 2, getTop() + PANEL_TITLE_HEIGHT_1 - 10, getScreenTitle().withColor(AbstractPanelScreen.TITLE_COLOR), TextWidget.Gravity.CENTER));

        this.globalButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.global_constraint_settings.title"), button -> {
            if (!getEntrance().getSessionManager().isSessionValid()) {
                getEntrance().getClient().execute(() -> {
                    new EffortlessSessionStatusScreen(getEntrance()).attach();
                });
            } else {
                getEntrance().getChannel().sendPacket(new PlayerPermissionCheckPacket(getEntrance().getClient().getPlayer().getId()), (packet) -> {
                    if (packet.granted()) {
                        getEntrance().getClient().execute(() -> {
                            new EffortlessGlobalConstraintSettingsScreen(getEntrance(), getEntrance().getSessionManager().getServerSessionConfigOrEmpty().getGlobalConfig(), config -> {
                                getEntrance().getSessionManager().updateGlobalConfig(config);
                            }).attach();
                        });
                    } else {
                        getEntrance().getClient().execute(() -> {
                            new EffortlessNotAnOperatorScreen(getEntrance()).attach();
                        });
                    }
                });
            }

        }).setBoundsGrid(getLeft(), getTop(), getWidth(), getHeight(), 2f, 0f, 1f).build());
        this.playerButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.player_constraint_settings.title"), button -> {
            getEntrance().getChannel().sendPacket(new PlayerPermissionCheckPacket(getEntrance().getClient().getPlayer().getId()), (packet) -> {

                if (!getEntrance().getSessionManager().isSessionValid()) {
                    getEntrance().getClient().execute(() -> {
                        new EffortlessSessionStatusScreen(getEntrance()).attach();
                    });
                } else {
                    if (packet.granted()) {
                        getEntrance().getClient().execute(() -> {
                            new EffortlessPlayerConstraintSettingsListScreen(getEntrance(), getEntrance().getSessionManager().getServerSessionConfigOrEmpty().playerConfigs(), playerConfigs -> {
                                getEntrance().getSessionManager().updatePlayerConfig(playerConfigs);
                            }).attach();
                        });
                    } else {
                        getEntrance().getClient().execute(() -> {
                            new EffortlessNotAnOperatorScreen(getEntrance()).attach();
                        });
                    }
                }
            });
        }).setBoundsGrid(getLeft(), getTop(), getWidth(), getHeight(), 1f, 0f, 1f).build());

        addWidget(Button.builder(getEntrance(), Text.translate("effortless.button.done"), button -> {
            detach();
        }).setBoundsGrid(getLeft(), getTop(), getWidth(), getHeight(), 0f, 0f, 1f).build());

    }

    @Override
    protected EffortlessClient getEntrance() {
        return (EffortlessClient) super.getEntrance();
    }

    @Override
    public void onReload() {
        var globalTooltip = new ArrayList<Text>();
        if (!Keys.KEY_LEFT_SHIFT.getBinding().isDown() && !Keys.KEY_LEFT_SHIFT.getBinding().isDown()) {
            globalTooltip.add(Text.translate("effortless.global_constraint_settings.title").withStyle(ChatFormatting.WHITE));
            globalTooltip.add(Lang.translate("tooltip.hold_for_summary", Lang.translateKeyDesc("shift").withStyle(ChatFormatting.DARK_GRAY)).withStyle(ChatFormatting.DARK_GRAY));
        } else {
            globalTooltip.add(Text.translate("effortless.global_constraint_settings.title").withStyle(ChatFormatting.WHITE));
            globalTooltip.add(Lang.translate("tooltip.hold_for_summary", Lang.translateKeyDesc("shift").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
            globalTooltip.add(Text.empty());
            globalTooltip.addAll(
                    TooltipHelper.wrapLines(getTypeface(), Text.translate("effortless.global_constraint_settings.tooltip", Text.text("[%s]".formatted(EffortlessConfigStorage.CONFIG_NAME)).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.GRAY))
            );
        }
        this.globalButton.setTooltip(globalTooltip);


        var playerTooltip = new ArrayList<Text>();
        if (!Keys.KEY_LEFT_SHIFT.getBinding().isDown() && !Keys.KEY_LEFT_SHIFT.getBinding().isDown()) {
            playerTooltip.add(Text.translate("effortless.player_constraint_settings.title").withStyle(ChatFormatting.WHITE));
            playerTooltip.add(Lang.translate("tooltip.hold_for_summary", Lang.translateKeyDesc("shift").withStyle(ChatFormatting.DARK_GRAY)).withStyle(ChatFormatting.DARK_GRAY));
        } else {
            playerTooltip.add(Text.translate("effortless.player_constraint_settings.title").withStyle(ChatFormatting.WHITE));
            playerTooltip.add(Lang.translate("tooltip.hold_for_summary", Lang.translateKeyDesc("shift").withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
            playerTooltip.add(Text.empty());
            playerTooltip.addAll(
                    TooltipHelper.wrapLines(getTypeface(), Text.translate("effortless.player_constraint_settings.tooltip", Text.text("[%s]".formatted(EffortlessConfigStorage.CONFIG_NAME)).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.GRAY))
            );
        }
        this.playerButton.setTooltip(playerTooltip);
    }
}
