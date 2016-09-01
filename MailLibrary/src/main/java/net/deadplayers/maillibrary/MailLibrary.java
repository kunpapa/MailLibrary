/*
 * Copyright 2016 kunpapa. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */
package net.deadplayers.maillibrary;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

/**
 * Main class of library
 * @author kunpapa
 *
 */
public class MailLibrary extends JavaPlugin
{

	private MailLibrary instance;
	private MailSender mailSender;
	private Metrics metrics;

	@Override
	public void onEnable(){
		instance = this;
		//load parameters
		this.saveDefaultConfig();
		this.enableMailSender();
		this.handleMetrics();

		Bukkit.getLogger().info("MailLibrary enabled correctly!");
	}
	
	private void enableMailSender(){
		mailSender = new MailSender(instance);
		mailSender.setMailTo(this.getConfig().getString("config.mailTo"));
		mailSender.setSenderName(this.getConfig().getString("config.senderName"));
		mailSender.setHost(this.getConfig().getString("config.host"));
		mailSender.setPort(this.getConfig().getString("config.port"));
		mailSender.setEmailAccount(this.getConfig().getString("config.emailAccount"));
		mailSender.setEmailPassword(this.getConfig().getString("config.emailPassword"));
		mailSender.initConnection();
	}
	
	private void handleMetrics(){
		if(this.getConfig().contains("metrics.enabled") && this.getConfig().getBoolean("metrics.enabled")){
			try {
				metrics = new Metrics(this);
				metrics.start();
			} catch (IOException e) {
				// Failed to submit the stats :-(
			}
		} else if (metrics != null){
			try {
				metrics.disable();
			} catch (IOException e) {
				// Failed to disable
			}
		}
	}
	
	private void reloadConfiguration(){
		this.reloadConfig();
		this.enableMailSender();
		this.handleMetrics();
	}

	@Override
	public void onDisable(){
		Bukkit.getLogger().info("MailLibrary disabled correctly!");
	}

	/**
	 * Method to send an ASYNC mail to default email
	 * @param subject subject of the email
	 * @param body body of mail, accepts text/html charset utf8
	 */
	public void sendMail(String subject, String body){
		mailSender.sendMail(subject,body);
	}

	/**
	 * Method to send an SYNC mail to default email
	 * @param subject subject of the email
	 * @param body body of mail, accepts text/html charset utf8
	 */
	public void sendMailSync(String subject, String body){
		mailSender.sendMailSync(subject,body);
	}
	
	/**
	 * Method to send an ASYNC mail
	 * @param mailTo email to send
	 * @param subject subject of the email
	 * @param body body of mail, accepts text/html charset utf8
	 */
	public void sendMail(String subject, String body, String mailTo){
		mailSender.sendMail(mailTo,subject,body);
	}

	/**
	 * Method to send an SYNC mail
	 * @param mailTo email to send
	 * @param subject subject of the email
	 * @param body body of mail, accepts text/html charset utf8
	 */
	public void sendMailSync(String subject, String body, String mailTo){
		mailSender.sendMailSync(mailTo,subject,body);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("maillibrary") && (sender.isOp() || sender.hasPermission("maillibrary.admin"))) {
			if(args.length>0){
				if(args[0].equalsIgnoreCase("testmail")){
					if(args.length>1){
						mailSender.sendMail(args[1], "Subject test", "Body <br> Test");
						sender.sendMessage(ChatColor.GOLD+"Testmail sent to "+args[1]);
					} else {
						mailSender.sendMail("Subject test", "body <br> Test");
						sender.sendMessage(ChatColor.GOLD+"Testmail sent to default mail");
					}
				}
				else if (args[0].equalsIgnoreCase("reload")){
					this.reloadConfiguration();
					sender.sendMessage(ChatColor.GOLD+"Plugin MailLibrary reloaded!");
				}
				else {
					sendHelp(sender);	
				}
			} else {
				sendHelp(sender);
			}
		}
		return true;
	}
	
	private void sendHelp(CommandSender sender){
		sender.sendMessage(ChatColor.RED+"MailLibrary Help:");
		sender.sendMessage(ChatColor.GOLD+"/maillibrary testmail -> test mail to default email");
		sender.sendMessage(ChatColor.GOLD+"/maillibrary testmail another@email.com -> test mail to another email");
		sender.sendMessage(ChatColor.GOLD+"/maillibrary reload -> reload configuration");
	}
}
