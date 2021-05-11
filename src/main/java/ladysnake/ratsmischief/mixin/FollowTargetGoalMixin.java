  
/*
 * Requiem
 * Copyright (C) 2017-2021 Ladysnake
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses>.
 *
 * Linking this mod statically or dynamically with other
 * modules is making a combined work based on this mod.
 * Thus, the terms and conditions of the GNU General Public License cover the whole combination.
 *
 * In addition, as a special exception, the copyright holders of
 * this mod give you permission to combine this mod
 * with free software programs or libraries that are released under the GNU LGPL
 * and with code included in the standard release of Minecraft under All Rights Reserved (or
 * modified versions of such code, with unchanged license).
 * You may copy and distribute such a system following the terms of the GNU GPL for this mod
 * and the licenses of the other code concerned.
 *
 * Note that people who make modified versions of this mod are not obligated to grant
 * this special exception for their modified versions; it is their choice whether to do so.
 * The GNU General Public License gives permission to release a modified version without this exception;
 * this exception also makes it possible to release a modified version which carries forward this exception.
 */
package ladysnake.ratsmischief.mixin;

import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.requiem.api.v1.possession.Possessable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(FollowTargetGoal.class)
public abstract class FollowTargetGoalMixin extends TrackTargetGoal {
    @Shadow @Nullable
    protected LivingEntity targetEntity;

    @Shadow protected TargetPredicate targetPredicate;

    @Shadow protected abstract Box getSearchBox(double double_1);

    public FollowTargetGoalMixin(MobEntity mobEntity_1, boolean boolean_1) {
        super(mobEntity_1, boolean_1);
    }

    @Inject(
            method = "findClosestTarget",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/entity/ai/goal/FollowTargetGoal;targetEntity:Lnet/minecraft/entity/LivingEntity;",
                    ordinal = 0,    // Fun fact: despite the decompiled source indicating that the player branch is the second, it's actually the first
                    shift = AFTER
            )
    )
    private void addTargets(CallbackInfo ci) {
        if (this.targetEntity == null) {
            RatEntity closestRat = this.mob.world.getClosestEntity(RatEntity.class, this.targetPredicate, this.mob, this.mob.getX(), this.mob.getY() + (double)this.mob.getStandingEyeHeight(), this.mob.getZ(), this.getSearchBox(this.getFollowRange()));
            if (((Possessable) closestRat).isBeingPossessed()) {
                this.targetEntity = closestRat;
            }
        }
    }
}