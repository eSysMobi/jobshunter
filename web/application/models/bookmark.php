<?php

class Bookmark extends DataMapper {
	var $model = 'bookmark';
	var $table = 'bookmarks';
	
	var $has_one = array();
	var $has_many = array();

	var $validation = array(
		'user_id' => array(
			'rules' => array('required', 'ingteger'),
			'label' => 'User Id'
		),
		'date' => array(
			'rules' => array('valid_date'),
			'label' => 'Date'
		),
		'site' => array(
			'rules' => array('required', 'valid_match' => array('hh', 'sj', 'rr')),
			'label' => 'Site'
		),
		'site_id' => array(
			'rules' => array('required'),
			'label' => 'Site id'
		),
		'name' => array(
			'rules' => array('required'),
			'label' => 'Name'
		),
		'url' => array(
			'rules' => array('required'),
			'label' => 'Url'
		),
		'city' => array(
			'rules' => array('required'),
			'label' => 'City'
		),
		'description' => array(
			'rules' => array('required'),
			'label' => 'Description'
		),
		'creation_date' => array(
			'rules' => array('required','valid_date'),
			'label' => 'Creation date'
		),
		'employer' => array(
			'rules' => array('required'),
			'label' => 'Employer'
		)
	);

	var $default_order_by = array('id' => 'desc');
 

    function __construct($id = NULL)
	{
		parent::__construct($id);
    }

	function post_model_init($from_cache = FALSE)
	{
	}
}