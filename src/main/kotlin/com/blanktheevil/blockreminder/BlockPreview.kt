package com.blanktheevil.blockreminder

import com.blanktheevil.blockreminder.patches.BlockPreviewFieldPatch
import com.blanktheevil.blockreminder.utils.scale
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.megacrit.cardcrawl.characters.AbstractPlayer
import com.megacrit.cardcrawl.core.AbstractCreature
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import com.megacrit.cardcrawl.helpers.FontHelper
import com.megacrit.cardcrawl.helpers.ImageMaster

class BlockPreview {
  companion object Statics {
    private val PREVIEW_COLOR = Color.CYAN.cpy()
    private const val BLOCK_W = 64
    private val BLOCK_ICON_X = (-14).scale()
    private val BLOCK_ICON_Y = (-14).scale()

    private fun increaseBlockPreview(owner: AbstractCreature, amount: Int) {
      with(BlockPreviewFieldPatch.blockPreview) {
        set(owner, (get(owner) ?: 0) + amount)
      }
    }

    @JvmField
    var isPreview = false

    @Suppress("unused")
    fun runPreview(amount: Int) {
      increaseBlockPreview(AbstractDungeon.player, amount)
    }

    fun update(instance: AbstractCreature) {
      isPreview = true
      PREVIEW_COLOR.a = 0.5f
      BlockPreviewFieldPatch.blockPreview.set(instance, 0)
      with(BlockReminder.endTurnBlockClasses) {
        if (instance is AbstractPlayer) {
          instance.powers.stream()
            .filter { this.contains(it::class.java.name) }
            .forEach {
              it.atEndOfTurn(true)
              it.atEndOfTurnPreEndTurnCards(true)
            }

          instance.orbs.stream()
            .filter { this.contains(it::class.java.name) }
            .forEach {
              it.onEndOfTurn()
            }

          instance.relics.stream()
            .filter { this.contains(it::class.java.name) }
            .forEach {
              it.onPlayerEndTurn()
            }
        }
      }
      isPreview = false
    }

    fun render(sb: SpriteBatch, x: Float, y: Float, offset: Float, currentBlock: Int, futureBlock: Int) {
      val yOffset: Float = if (currentBlock > 0) (BLOCK_W / 2.0f).scale() else 0.0f

      if (futureBlock <= 0) {
        return
      }

      sb.color = PREVIEW_COLOR
      sb.draw(
        ImageMaster.BLOCK_ICON,
        x + BLOCK_ICON_X - BLOCK_W / 2f,
        (y + BLOCK_ICON_Y - BLOCK_W / 2f) + offset + yOffset,
        BLOCK_W / 2f,
        BLOCK_W / 2f,
        BLOCK_W.toFloat(),
        BLOCK_W.toFloat(),
        1.scale(),
        1.scale(),
        0f,
        0,
        0,
        BLOCK_W,
        BLOCK_W,
        false,
        false)
      FontHelper.renderFontCentered(
        sb,
        FontHelper.blockInfoFont,
        "+$futureBlock",
        x + BLOCK_ICON_X,
        (y - 16f.scale()) + yOffset,
        Color(1f, 1f, 1f, 1f),
        1f)
    }
  }
}