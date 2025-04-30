package chylex.hee.mixins;

import static com.gtnewhorizon.gtnhlib.mixin.TargetedMod.VANILLA;

import java.util.List;
import java.util.function.Supplier;

import com.gtnewhorizon.gtnhlib.mixin.IMixins;
import com.gtnewhorizon.gtnhlib.mixin.ITargetedMod;
import com.gtnewhorizon.gtnhlib.mixin.MixinBuilder;
import com.gtnewhorizon.gtnhlib.mixin.Phase;
import com.gtnewhorizon.gtnhlib.mixin.Side;

public enum Mixins implements IMixins {

    REPLACE_DRAGON_EGG(new MixinBuilder("Replaces the dragon egg").setApplyIf(() -> true)
            .addMixinClasses("minecraft.MixinBlock").addTargetedMod(VANILLA).setSide(Side.BOTH).setPhase(Phase.EARLY));

    private final List<String> mixinClasses;
    private final List<ITargetedMod> targetedMods;
    private final List<ITargetedMod> excludedMods;
    private final Supplier<Boolean> applyIf;
    private final Phase phase;
    private final Side side;

    Mixins(MixinBuilder builder) {
        mixinClasses = builder.mixinClasses;
        targetedMods = builder.targetedMods;
        excludedMods = builder.excludedMods;
        applyIf = builder.applyIf;
        phase = builder.phase;
        side = builder.side;

        if (mixinClasses.isEmpty()) throw new RuntimeException("No mixin class specified for Mixin : " + name());
        if (targetedMods.isEmpty()) throw new RuntimeException("No targeted mods specified for Mixin : " + name());
        if (applyIf == null) throw new RuntimeException("No ApplyIf function specified for Mixin : " + name());
        if (phase == null) throw new RuntimeException("No Phase specified for Mixin : " + name());
        if (side == null) throw new RuntimeException("No Side function specified for Mixin : " + name());
    }

    @Override
    public List<String> getMixinClasses() {
        return mixinClasses;
    }

    @Override
    public Supplier<Boolean> getApplyIf() {
        return applyIf;
    }

    @Override
    public Phase getPhase() {
        return phase;
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public List<ITargetedMod> getTargetedMods() {
        return targetedMods;
    }

    @Override
    public List<ITargetedMod> getExcludedMods() {
        return excludedMods;
    }
}
