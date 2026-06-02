package kr.toxicity.damage.compatibility.modelengine

import com.ticxo.modelengine.api.ModelEngineAPI
import kr.toxicity.damage.api.adapter.ModelAdapter
import net.jodah.expiringmap.ExpirationPolicy
import net.jodah.expiringmap.ExpiringMap
import org.bukkit.entity.Entity
import java.util.concurrent.TimeUnit

class LegacyModelEngineAdapter : ModelAdapter {

    private val blueprintCache = ExpiringMap.builder()
        .maxSize(256)
        .expirationPolicy(ExpirationPolicy.ACCESSED)
        .expiration(1, TimeUnit.MINUTES)
        .build<String, Double>()

    override fun height(entity: Entity): Double? {
        return ModelEngineAPI.getModeledEntity(entity.uniqueId)?.run {
            models.values.maxOfOrNull {
                blueprintCache.computeIfAbsent(it.blueprint.name) { _ ->
                    it.blueprint.bones.values.maxOfOrNull { bb ->
                        bb.globalOrigin.y
                    } ?: 0.0
                }
            }
        }
    }
}