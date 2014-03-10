<?php

class subscription extends CI_Model {
	const SUB_CATEGORY = 1;
	const SUB_WORK = 2;
	const SUB_CITY = 3;
	private $subtypes = array('category' => 1, 'work' => 2, 'city' => 3);
	function __construct()
	{
		parent::__construct();
	}
	function add_subscription($user_id,$type,$sub_id) {
		if (is_string($type)) {
			$type = $this->subtypes[$type];
		}
		if (in_array($type, array(1,2,3))) {
			if ($this->db->select('user_id')->from('subscriptions')->where('user_id', $user_id)->where('sub_type', $type)->where('sub_id', $sub_id)->count_all_results()<0.5) {
				$this->db->insert('subscriptions', array('user_id' => $user_id,'sub_type' => $type,'sub_id' => $sub_id));
			}
			$this->make_subscriber($user_id);
			return 'success';
		} else {
			return 'undefined type';
		}
	}
	function remove_subscription($user_id,$type,$sub_id) {
		if (is_string($type)) {
			$type = $this->subtypes[$type];
		}
		if (in_array($type, array(1,2,3))) {
			if ($this->db->select('user_id')->from('subscriptions')->where('user_id', $user_id)->where('sub_type', $type)->where('sub_id', $sub_id)->count_all_results()>0.5) {
				$this->db->delete('subscriptions', array('user_id' => $user_id,'sub_type' => $type,'sub_id' => $sub_id));
			}
			return 'success';
		} else {
			return 'undefined type';
		}
	}
	function make_subscriber($user_id) {
		if ($this->db->select('id')->from('users')->where('id', $user_id)->where('subscriber', 1)->count_all_results()<0.5) {
			$this->db->where('id', $user_id)->update('users', array('subscriber' => 1));
		}
	}
	function unsubscribe($user_id) {
		$this->db->where('id', $user_id)->update('users', array('subscriber' => 0));
	}
	function add_gcmid($user_id,$gcm_id) {
		if ($this->db->insert('gcm_ids', array('user_id' => $user_id,'gcm_id' => $gcm_id))) {
			return true;
		} else {
			return false;
		}
	}
	function get_subscriptions() {
		$this->db->select('users_table.id as users_id, sub_table.sub_type as sub_type, sub_table.sub_id as sub_id, gcm_table.gcm_id as gcm_id');
		$this->db->from('users as users_table');
		$this->db->join('subscriptions as sub_table', 'users_table.id=sub_table.user_id');
		$this->db->join('gcm_ids as gcm_table', 'gcm_table.user_id = users_table.id');
		$this->db->where('users_table.subscriber',1);
		$result = $this->db->get()->result();
	}
}