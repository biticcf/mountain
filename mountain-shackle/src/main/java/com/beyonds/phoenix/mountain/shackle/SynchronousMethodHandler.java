/**
 * 
 */
package com.beyonds.phoenix.mountain.shackle;

import com.beyonds.phoenix.mountain.shackle.InvocationHandlerFactory.MethodHandler;

/**
 * author: Daniel.Cao
 * date: 2019年1月3日
 * time: 下午11:45:37
 *
 */
final class SynchronousMethodHandler implements MethodHandler {

	private final MethodMetadata metadata;
	private final Target<?> target;
	private final DomainExecutor domainExecutor;
	private final DomainTemplate.Factory buildTemplateFromArgs;

	private SynchronousMethodHandler(Target<?> target, DomainExecutor domainExecutor, MethodMetadata metadata,
			DomainTemplate.Factory buildTemplateFromArgs) {
		this.target = Util.checkNotNull(target, "target");
		this.domainExecutor = Util.checkNotNull(domainExecutor, "domainExecutor for %s", target);
		this.metadata = Util.checkNotNull(metadata, "metadata for %s", target);
		this.buildTemplateFromArgs = Util.checkNotNull(buildTemplateFromArgs, "metadata for %s", target);
	}

	@Override
	public Object invoke(Object[] argv) throws Throwable {
		DomainTemplate template = buildTemplateFromArgs.create(argv);

		return execute(template, argv);
	}

	Object execute(DomainTemplate template, Object[] args) throws Throwable {
		Domain domain = targetDomain(template);

		return domainExecutor.execute(domain, args, metadata.argTypes());
	}

	Domain targetDomain(DomainTemplate template) {
		return target.apply(new DomainTemplate(template));
	}

	static class Factory {

		private final DomainExecutor domainExecutor;

		Factory(DomainExecutor domainExecutor) {
			this.domainExecutor = Util.checkNotNull(domainExecutor, "domainExecutor");
		}

		public MethodHandler create(Target<?> target, MethodMetadata md, DomainTemplate.Factory buildTemplateFromArgs) {
			return new SynchronousMethodHandler(target, domainExecutor, md, buildTemplateFromArgs);
		}
	}
}
