package com.example.dmstaskmanager.view.dynamic

class DynamicsCircleDiagram(springiness: Float, damping: Float) {

    /** Коэффициент упругости */
    private var springiness = 0f

    /** Коэффициент затухания */
    private var damping = 0f

    init {
        this.springiness = springiness
        this.damping = damping
    }

    companion object {
        private const val TOLERANCE = 0.01f
        const val STEP = 11
    }

    /** Конечная позиция точки*/
    var targetPosition = 0f

    /** Текущая позиция точки*/
    var position = 0f

    /** Текущая скорость точки */
    private var velocity = 0f

    /** Время последнего обновления */
    private var lastTime = 0L

//    fun update(now: Long) {
//        val dt = Math.min(now - lastTime, 50)
//        velocity += (targetPosition - position) * springiness
//        velocity *= (1 - damping)
//        position += velocity * dt / 1000
//        lastTime = now
//    }

    fun update(now: Long) {
//       val dt = Math.min(now - lastTime, 50)
//        velocity += (targetPosition - position) * springiness
//        velocity *= (1 - damping)
        position = Math.min(position + STEP, targetPosition)
        lastTime = now
    }

    fun isAtRest(): Boolean {
        val standingStill: Boolean = Math.abs(velocity) < TOLERANCE
        val isAtTarget: Boolean = targetPosition - position < TOLERANCE
        return standingStill && isAtTarget
    }

    fun setState(position: Float, lastTime: Long) {
        this.position = position
        this.lastTime = lastTime
    }
}

