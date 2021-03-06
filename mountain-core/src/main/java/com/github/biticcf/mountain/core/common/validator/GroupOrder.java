/**
 * 
 */
package com.github.biticcf.mountain.core.common.validator;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

/**
 * author: DanielCao
 * date:   2018年7月8日
 * time:   上午12:33:57
 *
 */
@GroupSequence({GroupCreate.class, GroupUpdate.class, GroupQuery.class, GroupDelete.class, GroupDefault.class, Default.class})
public interface GroupOrder {

}
