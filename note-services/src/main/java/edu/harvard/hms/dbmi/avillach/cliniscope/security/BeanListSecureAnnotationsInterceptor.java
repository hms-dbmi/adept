package edu.harvard.hms.dbmi.avillach.cliniscope.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.common.util.ClassHelper;
import org.apache.cxf.interceptor.security.SecureAnnotationsInterceptor;

public class BeanListSecureAnnotationsInterceptor extends SecureAnnotationsInterceptor {
    private static final Logger LOG = LogUtils.getL7dLogger(BeanListSecureAnnotationsInterceptor.class);
    
    public void setSecuredBeans(List<Object> secureBeans){
		Map<String, String> rolesMap = new HashMap<String, String>();

		for(Object bean : secureBeans){
			Class<?> cls = ClassHelper.getRealClass(bean);
			findRoles(cls, rolesMap);
			if (rolesMap.isEmpty()) {
				LOG.warning("The roles map is empty, the service object is not protected : " + cls.getName());
			} else if (LOG.isLoggable(Level.FINE)) {
				LOG.warning("Securing : " + cls.getName());
				for (Map.Entry<String, String> entry : rolesMap.entrySet()) {
					LOG.fine("Method: " + entry.getKey() + ", roles: " + entry.getValue());
				}
			}
		}
		super.setMethodRolesMap(rolesMap);
	}
}
