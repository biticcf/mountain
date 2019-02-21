/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.validator;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

/**
 * @Author: DanielCao
 * @Date:   2018年7月8日
 * @Time:   上午12:33:57
 *
 */
@GroupSequence({GroupCreate.class, GroupUpdate.class, GroupQuery.class, GroupDelete.class, GroupDefault.class, Default.class})
public interface GroupOrder {

}
