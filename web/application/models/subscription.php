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
	function get_subscriptions($get_gcms=true, $user_id=false) {
		$this->db->select('users_table.id as users_id,users_table.seen as seen, sub_table.sub_type as sub_type, sub_table.sub_id as sub_id'.($get_gcms?', gcm_table.gcm_id as gcm_id':''));
		$this->db->from('users as users_table');
		$this->db->join('subscriptions as sub_table', 'users_table.id=sub_table.user_id');
		if ($get_gcms) {
			$this->db->join('gcm_ids as gcm_table', 'gcm_table.user_id = users_table.id');
		}
		$this->db->where('users_table.subscriber',1);
		if ($user_id) {
			$this->db->where('users_table.id',$user_id);
		}
		$result = $this->db->get()->result();
		return $result;
	}
	function count_new_vacancies_for_sub($sub) {
		switch($sub->sub_type) {
			case self::SUB_CATEGORY:
				$this->db->like('categories', "\"{$sub->sub_id}\"");
				break;
			case self::SUB_WORK:
				$this->db->like('worktype', $sub->sub_id);
				break;
			case self::SUB_CITY:
				$this->db->like('city', "\"{$sub->sub_id}\"");
				break;
		}
		$this->db->where('parse_date >',$sub->seen);
		$this->db->from('vacancies');
		return $this->db->count_all_results();
	}
	function send_amounts($to_send) {
		foreach($to_send as $rec=>$amount) {
			$this->send_message($rec,'vacancy',array('amount' => $amount));
		}
	}
	function send_message($recipient,$text,$data) {
		$this->load->library('gcm');
		$this->gcm->setMessage($text);
        $this->gcm->addRecepient($recipient);
        $this->gcm->setData($data);
        $this->gcm->setTtl(500);
        $this->gcm->setTtl(false);
        $this->gcm->setGroup('Test');
        $this->gcm->setGroup(false);
		if (!$this->gcm->send())
			log_message('error', 'Message to '.$recipient.' wasnt sent.');
	}
	function get_new_vacancies_by_subs($subs=array(),$count=false,$offset=false,$limit=false) {
		$first=true;
		foreach($subs as $sub) {
			switch($sub->sub_type) {
				case self::SUB_CATEGORY:
					$this->db->like('categories', "\"{$sub->sub_id}\"");
					break;
				case self::SUB_WORK:
					$this->db->like('worktype', $sub->sub_id);
					break;
				case self::SUB_CITY:
					$this->db->like('city', "\"{$sub->sub_id}\"");
					break;
			}
		}
		$this->db->order_by("date", "desc");
		$this->db->from('vacancies');
		if ($offset && $limit) {
			$this->db->limit($offset, $limit);
		}
		if ($count) {
			$result = $this->db->count_all_results();
		} else {
			$result = $this->db->get()->result();
		}
		return $result;
	}
}