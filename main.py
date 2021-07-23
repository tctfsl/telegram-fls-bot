from dotenv import load_dotenv
from dotenv.main import dotenv_values
load_dotenv()

from telegram.ext import Updater

updater = Updater(token='1302957992:AAGHAAcJgNBkbd6pSsUbLW85dpxSHBHRkrA', use_context=True)

dispatcher = updater.dispatcher

import logging
logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
                     level=logging.INFO)

def start(update, context):
    context.bot.send_message(chat_id=update.effective_chat.id, text="I'm a bot, please talk to me!")

def custom(update, context):
    context.bot.send_message(chat_id=update.effective_chat.id, text="Custom text")

from telegram.ext import CommandHandler
start_handler = CommandHandler('start', start)
custom_handler = CommandHandler('custom', custom)

dispatcher.add_handler(start_handler)
dispatcher.add_handler(custom_handler)

updater.start_polling()