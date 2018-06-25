package biodiv.userGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;

import biodiv.userGroup.userGroupMemberRole.UserGroupMemberRole;
import biodiv.userGroup.userGroupMemberRole.UserGroupMemberRoleDao;
import biodiv.userGroup.userGroupMemberRole.UserGroupMemberRoleService;

public class UserGroupModule extends ServletModule {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected void configureServlets() {
		log.debug("Configuring UserGroupModule Servlets");
		bind(UserGroup.class);
		bind(UserGroupDao.class).in(Singleton.class);
		bind(UserGroupService.class).in(Singleton.class);
		bind(UserGroupController.class).in(Singleton.class);

		bind(Newsletter.class);
		bind(NewsletterDao.class).in(Singleton.class);
		bind(NewsletterService.class).in(Singleton.class);

		bind(UserGroupMemberRole.class);
		bind(UserGroupMemberRoleDao.class).in(Singleton.class);
		bind(UserGroupMemberRoleService.class).in(Singleton.class);

		bind(UserGroupMailingService.class).in(Singleton.class);

		bind(AclClass.class);
		bind(AclEntry.class);
		bind(AclSid.class);
		bind(AclObjectIdentity.class);
		bind(AclLookupStrategy.class).in(Singleton.class);
		bind(AclService.class).in(Singleton.class);
		bind(AclUtilService.class).in(Singleton.class);
		bind(CustomObjectIdentityRetrievalStrategy.class).in(Singleton.class);
	}

	@Provides
	@Singleton
	protected AclAuthorizationStrategy provideAclAuthorizationStrategy(SidRetrievalStrategy sidRetrievalStrategy) {
		AclAuthorizationStrategyImpl aclAuthorizationStrategy = new AclAuthorizationStrategyImpl(AuthorityUtils
				.createAuthorityList("ROLE_ADMIN", "ROLE_ADMIN", "ROLE_RUN_AS_ADMIN").toArray(new GrantedAuthority[0]));
		aclAuthorizationStrategy.setSidRetrievalStrategy(sidRetrievalStrategy);
		return aclAuthorizationStrategy;
	}

	@Provides
	@Singleton
	protected SidRetrievalStrategy provideSidRetrievalStrategy() {
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		//TODO:initialize Role hierarchy
		roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
		SidRetrievalStrategyImpl sidRetrievalStrategy = new SidRetrievalStrategyImpl(roleHierarchy);
		return sidRetrievalStrategy;
	}

	@Provides
	@Singleton
	protected PermissionFactory providePermissionFactory() {
		PermissionFactory permissionFactory = new  DefaultPermissionFactory(BasePermission.class);
		return permissionFactory;
	}
		
	@Provides
	@Singleton
	protected PermissionGrantingStrategy providePermissionGrantingStrategy() {
		PermissionGrantingStrategy permissionGrantingStrategy = new  DefaultPermissionGrantingStrategy(new NullAclAuditLogger());
		return permissionGrantingStrategy;
	}
	
	@Provides
	@Singleton
	protected PermissionEvaluator providePermissionEvaluator(AclService aclService) {
		AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService);
		return permissionEvaluator;
	}
	
	@Provides
	@Singleton
	protected ObjectIdentityRetrievalStrategy provideObjectIdentityRetrievalStrategy() {
		ObjectIdentityRetrievalStrategy objectIdentityRetrievalStrategy = new CustomObjectIdentityRetrievalStrategy();
		return objectIdentityRetrievalStrategy;
	}

	@Provides
	@Singleton
	protected AclCache provideAclCache(PermissionGrantingStrategy permissionGrantingStrategy, AclAuthorizationStrategy aclAuthorizationStrategy) {
		EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
		ehCacheManagerFactoryBean.setCacheManagerName("biodiv-api-acl-cache");
		ehCacheManagerFactoryBean.afterPropertiesSet();
		
		EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
		ehCacheFactoryBean.setCacheManager(ehCacheManagerFactoryBean.getObject());
		ehCacheFactoryBean.setCacheName("aclCache");
		ehCacheFactoryBean.afterPropertiesSet();
		System.out.println(ehCacheFactoryBean.getObject());
		System.out.println("-------------------------------------");
		System.out.println("-------------------------------------");
		System.out.println("-------------------------------------");
		System.out.println("-------------------------------------");
		System.out.println("-------------------------------------");
		AclCache ehCacheBasedAclCache = new EhCacheBasedAclCache(ehCacheFactoryBean.getObject(), permissionGrantingStrategy, aclAuthorizationStrategy);
		return ehCacheBasedAclCache;
	}
}
