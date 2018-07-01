package biodiv.userGroup;

import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.model.AccessControlEntry;
            
/**
 * No-op logger that gets registered as the logger so there's a bean to override.
 *
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */         
public class NullAclAuditLogger implements AuditLogger {
    public void logIfNeeded(final boolean granted, final AccessControlEntry ace) {
        // do nothing
    }
} 

