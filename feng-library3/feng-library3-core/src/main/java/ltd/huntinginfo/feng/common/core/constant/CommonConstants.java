/*
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ltd.huntinginfo.feng.common.core.constant;

/**
 * @author lengleng
 * @date 2019/2/1
 */
public interface CommonConstants {
    /**
     * 租户头
     */
    String TENANT_HEADER_KEY = "J-Cat";
    /**
     * 租户ID
     */
    String TENANT_ID = "jCat";
    /**
     * 默认租户ID
     */
    String DEFAULT_TENANT_ID = "100";
    
    /**
     * 默认租户编码
     */
    String DEFAULT_TENANT_CODE = "default";
    
    /**
     * 租户头默认密钥
     */
    String DEFAULT_TENANT_CRYPT_KEY = "fengyonghua4java";
    /**
     * Header 中版本信息
     */
    String VERSION = "VERSION";

    /**
     * 机构编码
     */
    String ORGAN_CODE = "organCode";
    /**
     * 默认机构编码
     */
    String ORGAN_CODE_ADMIN = "F001";
    
	/**
	 * 删除
	 */
	String STATUS_DEL = "1";

	/**
	 * 正常
	 */
	String STATUS_NORMAL = "0";

	/**
	 * 锁定
	 */
	String STATUS_LOCK = "9";

	/**
	 * 菜单树根节点
	 */
	String MENU_TREE_ROOT_ID = "-1";

	/**
	 * 菜单
	 */
	String MENU = "0";

	/**
	 * 编码
	 */
	String UTF8 = "UTF-8";

	/**
	 * JSON 资源
	 */
	String CONTENT_TYPE = "application/json; charset=utf-8";

	/**
	 * 前端工程名
	 */
	String FRONT_END_PROJECT = "feng-cloud3-ui";

	/**
	 * 后端工程名
	 */
	String BACK_END_PROJECT = "feng-cloud3";

	/**
	 * 成功标记
	 */
	Integer SUCCESS = 0;

	/**
	 * 失败标记
	 */
	Integer FAIL = 1;

	/**
	 * 当前页
	 */
	String CURRENT = "current";

	/**
	 * size
	 */
	String SIZE = "size";

	/**
	 * 用户名
	 */
	String USERNAME = "username";

	/**
	 * 密码
	 */
	String PASSWORD = "password";

	/**
	 * 请求开始时间
	 */
	String REQUEST_START_TIME = "REQUEST-START-TIME";

}
