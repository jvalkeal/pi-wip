/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.iot.support;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;

/**
 * Convenient base class for object which needs spring task scheduler, task
 * executor and life cycle handling.
 *
 * @author Janne Valkealahti
 *
 */
public abstract class LifecycleObjectSupport implements InitializingBean, DisposableBean, SmartLifecycle {

	private static final Logger log = LoggerFactory.getLogger(LifecycleObjectSupport.class);

	// fields for lifecycle
	private volatile boolean autoStartup = false;
	private volatile int phase = 0;
	private volatile boolean running;

	// lock to protect lifycycle methods
	private final ReentrantLock lifecycleLock = new ReentrantLock();

	// protect InitializingBean for single call
	private final AtomicBoolean afterPropertiesSetCalled = new AtomicBoolean(false);

	@Override
	public final void afterPropertiesSet() {
		try {
			if (afterPropertiesSetCalled.compareAndSet(false, true)) {
				this.onInit();
			} else {
				log.debug("afterPropertiesSet() is already called, not calling onInit()");
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new BeanInitializationException("failed to initialize", e);
		}
	}

	@Override
	public final void destroy() throws Exception {
		log.info("destroy called");
		doDestroy();
	}

	@Override
	public final boolean isAutoStartup() {
		return this.autoStartup;
	}

	@Override
	public final int getPhase() {
		return this.phase;
	}

	@Override
	public final boolean isRunning() {
		this.lifecycleLock.lock();
		try {
			return this.running;
		} finally {
			this.lifecycleLock.unlock();
		}
	}

	@Override
	public final void start() {
		this.lifecycleLock.lock();
		try {
			if (!this.running) {
				this.running = true;
				this.doStart();
				if (log.isInfoEnabled()) {
					log.info("started " + this);
				} else {
					if(log.isDebugEnabled()) {
						log.debug("already started " + this);
					}
				}
			}
		} finally {
			this.lifecycleLock.unlock();
		}
	}

	@Override
	public final void stop() {
		this.lifecycleLock.lock();
		try {
			if (this.running) {
				this.doStop();
				this.running = false;
				if (log.isInfoEnabled()) {
					log.info("stopped " + this);
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("already stopped " + this);
				}
			}
		} finally {
			this.lifecycleLock.unlock();
		}
	}

	@Override
	public final void stop(Runnable callback) {
		this.lifecycleLock.lock();
		try {
			this.stop();
			callback.run();
		} finally {
			this.lifecycleLock.unlock();
		}
	}

	/**
	 * Sets the auto startup.
	 *
	 * @param autoStartup the new auto startup
	 * @see SmartLifecycle
	 */
	public void setAutoStartup(boolean autoStartup) {
		this.autoStartup = autoStartup;
	}

	/**
	 * Sets the phase.
	 *
	 * @param phase the new phase
	 * @see SmartLifecycle
	 */
	public void setPhase(int phase) {
		this.phase = phase;
	}

	/**
	 * Subclasses may implement this for initialization logic. Called
	 * during the {@link InitializingBean} phase. Implementor should
	 * always call super method not to break initialization chain.
	 *
	 * @throws Exception exception
	 */
	protected void onInit() throws Exception {}

	/**
	 * Subclasses may implement this method with the start behavior. This
	 * method will be invoked while holding the {@link #lifecycleLock}.
	 */
	protected void doStart() {};

	/**
	 * Subclasses may implement this method with the stop behavior. This method
	 * will be invoked while holding the {@link #lifecycleLock}.
	 */
	protected void doStop() {};

	/**
	 * Subclasses may implement this method with the destroy behavior.
	 */
	protected void doDestroy() {};

}
